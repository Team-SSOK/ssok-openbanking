package kr.ssok.ssokopenbanking.transfer.service;

import kr.ssok.ssokopenbanking.global.exception.CustomException;
import kr.ssok.ssokopenbanking.transfer.dto.request.TransferRequestDto;
import kr.ssok.ssokopenbanking.transfer.dto.response.TransferResponseDto;
import kr.ssok.ssokopenbanking.transfer.entity.Transaction;
import kr.ssok.ssokopenbanking.transfer.enums.TransactionStatus;
import kr.ssok.ssokopenbanking.transfer.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.concurrent.CompletableFuture;

@Service
@RequiredArgsConstructor
@Slf4j
public class TransferServiceImpl implements TransferService {

    private final BankApiService bankApiService;
    private final TransactionRepository transactionRepository;

    @Transactional
    @Override
    public TransferResponseDto processTransfer(TransferRequestDto dto) {
        // 1. 트랜잭션 생성 및 상태 기록
        Transaction tx = Transaction.create(dto);
        tx.updateStatus(TransactionStatus.REQUESTED);
        transactionRepository.save(tx);

        try {
            // 2. 유효성 및 휴면 계좌 체크 (CompletableFuture 병렬 처리)
            CompletableFuture<Void> validateSendAccount = CompletableFuture.runAsync(() -> {
                bankApiService.validateAccount(dto.getSendAccountNumber(), dto.getSendBankCode());
                bankApiService.checkDormant(dto.getSendAccountNumber(), dto.getSendBankCode());
            });

            CompletableFuture<Void> validateRecvAccount = CompletableFuture.runAsync(() -> {
                bankApiService.validateAccount(dto.getRecvAccountNumber(), dto.getRecvBankCode());
                bankApiService.checkDormant(dto.getRecvAccountNumber(), dto.getRecvBankCode());
            });

            CompletableFuture.allOf(validateSendAccount, validateRecvAccount).join();

            tx.updateStatus(TransactionStatus.VALIDATED);

            // 3. 잔액 체크
            bankApiService.checkBalance(dto.getSendAccountNumber(), dto.getSendBankCode(), dto.getAmount());

            // 4. 출금 요청
            tx.updateStatus(TransactionStatus.WITHDRAW_REQUESTED);
            bankApiService.withdraw(dto);
            tx.updateStatus(TransactionStatus.WITHDRAW_SUCCESS);

            // 5. 입금 요청
            tx.updateStatus(TransactionStatus.DEPOSIT_REQUESTED);
            bankApiService.deposit(dto);
            tx.updateStatus(TransactionStatus.DEPOSIT_SUCCESS);

            // 6. 송금 완료
            tx.updateStatus(TransactionStatus.COMPLETED);

            return TransferResponseDto.builder()
                    .transactionId(tx.getTransactionId().toString())
                    .status(tx.getStatus().name())
                    .message("송금이 성공적으로 처리되었습니다.")
                    .build();

        } catch (CustomException e) {
            log.error("[송금 실패] 비즈니스 예외 발생 - 사유: {}", e.getMessage());
            handleFailedTransaction(tx, dto, e);

            return TransferResponseDto.builder()
                    .transactionId(tx.getTransactionId().toString())
                    .status(tx.getStatus().name())
                    .message(e.getMessage()) // CustomException은 메시지 노출 OK
                    .build();

        } catch (Exception e) {
            log.error("[송금 실패] 시스템 예외 발생 - 사유: {}", e.getMessage(), e);
            handleFailedTransaction(tx, dto, e);

            return TransferResponseDto.builder()
                    .transactionId(tx.getTransactionId().toString())
                    .status(tx.getStatus().name())
                    .message("송금 처리 중 예상치 못한 오류가 발생했습니다.") // 고정 메시지 사용
                    .build();
        }
    }

    /**
     * 송금 실패 시 보상 처리 및 트랜잭션 상태 업데이트
     */
    private void handleFailedTransaction(Transaction tx, TransferRequestDto dto, Exception e) {
        if (TransactionStatus.WITHDRAW_SUCCESS.equals(tx.getStatus()) ||
                TransactionStatus.DEPOSIT_REQUESTED.equals(tx.getStatus())) {

            boolean compensated = bankApiService.compensate(dto);
            if (compensated) {
                tx.updateStatus(TransactionStatus.COMPENSATED);
                log.info("[보상 성공] 출금 복구 완료 - 계좌: {}", dto.getSendAccountNumber());
            } else {
                tx.updateStatus(TransactionStatus.COMPENSATION_FAILED);
                log.error("[보상 실패] 출금 복구 실패 - 계좌: {}", dto.getSendAccountNumber());
            }
        } else {
            tx.updateStatus(TransactionStatus.FAILED);
        }
    }
}
