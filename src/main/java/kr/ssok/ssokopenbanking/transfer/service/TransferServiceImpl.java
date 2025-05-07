package kr.ssok.ssokopenbanking.transfer.service;

import kr.ssok.ssokopenbanking.global.comm.KafkaCommModule;
import kr.ssok.ssokopenbanking.global.exception.CustomException;
import kr.ssok.ssokopenbanking.global.exception.TransferException;

import kr.ssok.ssokopenbanking.transfer.dto.request.TransferRequestDto;
import kr.ssok.ssokopenbanking.transfer.dto.request.ValidateAccountRequestDto;
import kr.ssok.ssokopenbanking.transfer.dto.request.CheckDormantRequestDto;
import kr.ssok.ssokopenbanking.transfer.dto.request.CheckBalanceRequestDto;
import kr.ssok.ssokopenbanking.transfer.dto.response.TransferResponseDto;
import kr.ssok.ssokopenbanking.transfer.entity.Transaction;
import kr.ssok.ssokopenbanking.transfer.enums.TransactionStatus;
import kr.ssok.ssokopenbanking.transfer.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;

import static kr.ssok.ssokopenbanking.transfer.mapper.TransferMapper.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class TransferServiceImpl implements TransferService {

    private final BankApiService bankApiService;
    private final TransactionRepository transactionRepository;
    private final KafkaCommModule commModule;

    /**
     * 송금 요청 처리: 계좌 검증 → 잔액 확인 → 출금 → 입금 → 완료
     */
    @Transactional
    @Override
    public TransferResponseDto processTransfer(TransferRequestDto dto) {
        // 1. 트랜잭션 생성 및 초기 상태 저장
        Transaction tx = Transaction.create(dto);
        tx.updateStatus(TransactionStatus.REQUESTED);
        transactionRepository.save(tx);

        try {
            // 트랜잭션 ID 문자열 가져오기
            String txId = tx.getTransactionId().toString();

            // 2. 계좌 유효성 및 휴면 여부 병렬 체크 (출금자, 입금자)
            CompletableFuture<Void> validateSendAccount = CompletableFuture.runAsync(() -> {
                validateAccountAndDormant(txId, dto.getSendName(), dto.getSendAccountNumber());
            });

            CompletableFuture<Void> validateRecvAccount = CompletableFuture.runAsync(() -> {
                validateAccountAndDormant(txId, dto.getRevName(), dto.getRecvAccountNumber());
            });

            CompletableFuture.allOf(validateSendAccount, validateRecvAccount).join();
            tx.updateStatus(TransactionStatus.VALIDATED);

            // 3. 잔액 확인
            bankApiService.checkBalance(txId, CheckBalanceRequestDto.builder()
                    .account(dto.getSendAccountNumber())
                    .build());

            // 4. 출금 요청
            tx.updateStatus(TransactionStatus.WITHDRAW_REQUESTED);
            bankApiService.withdraw(txId, toWithdrawRequest(tx));
            tx.updateStatus(TransactionStatus.WITHDRAW_SUCCESS);

            // 5. 입금 요청
            tx.updateStatus(TransactionStatus.DEPOSIT_REQUESTED);
            bankApiService.deposit(txId, toDepositRequest(tx));
            tx.updateStatus(TransactionStatus.DEPOSIT_SUCCESS);

            // 6. 송금 완료
            tx.updateStatus(TransactionStatus.COMPLETED);
            return toResponse(tx, "송금이 성공적으로 처리되었습니다.");

        } catch (Exception e) {
            Throwable cause = (e instanceof CompletionException) ? e.getCause() : e;

            // 상태 업데이트 및 보상 처리
            handleFailedTransaction(tx, dto, cause instanceof Exception ? (Exception) cause : new Exception(cause));

            // 예외는 그대로 → GlobalExceptionHandler 처리
            if (cause instanceof RuntimeException) throw (RuntimeException) cause;
            throw new RuntimeException(cause);

            /*
            // 예외 발생 시 비즈니스 or 시스템 예외로 분기
            return handleException(e, tx, dto);
            */
        }
    }

    /**
     * 계좌 유효성 + 휴면 계좌 여부 검사 (두 검사를 묶어서 수행)
     */
    private void validateAccountAndDormant(String transactionId, String name, String accountNumber) {
        bankApiService.validateAccount(
                transactionId,
                ValidateAccountRequestDto.builder()
                        .username(name)
                        .account(accountNumber)
                        .build()
        );

        bankApiService.checkDormant(
                transactionId,
                CheckDormantRequestDto.builder()
                        .accountNumber(accountNumber)
                        .build()
        );
    }

    /**
     * 실패 시 보상 처리 로직 수행
     * - 출금만 성공하고 입금 실패한 경우: 보상 입금 요청
     */
    private void handleFailedTransaction(Transaction tx, TransferRequestDto dto, Exception e) {
        if (TransactionStatus.WITHDRAW_SUCCESS.equals(tx.getStatus()) ||
                TransactionStatus.DEPOSIT_REQUESTED.equals(tx.getStatus())) {

            System.out.println("11111");

            // 복구 입금(보상) 처리
            boolean compensated = bankApiService.compensate(
                    tx.getTransactionId().toString(),
                    toDepositRequest(tx)
            );

            System.out.println(compensated + " TransferServiceImpl");

            if (compensated) {
                tx.updateStatus(TransactionStatus.COMPENSATED);
                log.info("[보상 성공] 출금 복구 완료 - 계좌: {}", dto.getSendAccountNumber());
            } else {
                tx.updateStatus(TransactionStatus.COMPENSATION_FAILED);
                log.error("[보상 실패] 출금 복구 실패 - 계좌: {}", dto.getSendAccountNumber());
            }
        } else {
            // 출금도 실패한 경우는 단순 실패로 처리
            tx.updateStatus(TransactionStatus.FAILED);
        }
    }
}
