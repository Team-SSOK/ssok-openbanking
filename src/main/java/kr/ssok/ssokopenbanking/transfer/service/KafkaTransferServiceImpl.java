package kr.ssok.ssokopenbanking.transfer.service;

import kr.ssok.ssokopenbanking.global.comm.CommunicationProtocol;
import kr.ssok.ssokopenbanking.global.comm.KafkaCommModule;
import kr.ssok.ssokopenbanking.global.comm.promise.CommQueryPromise;
import kr.ssok.ssokopenbanking.global.comm.promise.PromiseMessage;
import kr.ssok.ssokopenbanking.transfer.dto.request.*;
import kr.ssok.ssokopenbanking.transfer.dto.response.TransferResponseDto;
import kr.ssok.ssokopenbanking.transfer.entity.Transaction;
import kr.ssok.ssokopenbanking.transfer.enums.TransactionStatus;
import kr.ssok.ssokopenbanking.transfer.mapper.TransferMapper;
import kr.ssok.ssokopenbanking.transfer.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;

@Slf4j
@Service
@RequiredArgsConstructor
@Profile("kafka")
public class KafkaTransferServiceImpl implements TransferService {

    private final KafkaCommModule commModule;
    private final TransactionRepository transactionRepository;
    private final BankApiService bankApiService;

    @Transactional
    @Override
    public TransferResponseDto processTransfer(TransferRequestDto dto) {
        Transaction tx = Transaction.create(dto);
        tx.updateStatus(TransactionStatus.REQUESTED);
        transactionRepository.save(tx);

        try {
            String txId = tx.getTransactionId().toString();

            // 계좌 유효성 및 휴면 검사 (기존 REST API)
            CompletableFuture<Void> validateSend = CompletableFuture.runAsync(() -> {
                validateAccountAndDormant(txId, dto.getSendName(), dto.getSendAccountNumber());
            });

            CompletableFuture<Void> validateRecv = CompletableFuture.runAsync(() -> {
                validateAccountAndDormant(txId, dto.getRecvName(), dto.getRecvAccountNumber());
            });

            CompletableFuture.allOf(validateSend, validateRecv).join();
            tx.updateStatus(TransactionStatus.VALIDATED);

            // 잔액 확인 (기존 REST API)
            bankApiService.checkBalance(txId, CheckBalanceRequestDto.builder()
                    .account(dto.getSendAccountNumber())
                    .build());

            // 출금 요청 (Kafka)
            tx.updateStatus(TransactionStatus.WITHDRAW_REQUESTED);
            commModule.sendPromiseQuery(CommunicationProtocol.REQUEST_WITHDRAW, TransferMapper.toWithdrawRequest(tx)).get();
            tx.updateStatus(TransactionStatus.WITHDRAW_SUCCESS);

            // 입금 요청 (Kafka)
            tx.updateStatus(TransactionStatus.DEPOSIT_REQUESTED);
            commModule.sendPromiseQuery(CommunicationProtocol.REQUEST_DEPOSIT, TransferMapper.toDepositRequest(tx)).get();
            tx.updateStatus(TransactionStatus.DEPOSIT_SUCCESS);

            tx.updateStatus(TransactionStatus.COMPLETED);
            return TransferMapper.toResponse(tx, "송금이 성공적으로 처리되었습니다.");

        } catch (Exception e) {
            Throwable cause = (e instanceof CompletionException) ? e.getCause() : e;
            handleFailedTransaction(tx, dto, cause instanceof Exception ? (Exception) cause : new Exception(cause));
            throw new RuntimeException(cause);
        }
    }

    private void validateAccountAndDormant(String transactionId, String name, String accountNumber) {
        bankApiService.validateAccount(
                transactionId,
                ValidateAccountRequestDto.builder().username(name).account(accountNumber).build()
        );

        bankApiService.checkDormant(
                transactionId,
                CheckDormantRequestDto.builder().accountNumber(accountNumber).build()
        );
    }

    private void handleFailedTransaction(Transaction tx, TransferRequestDto dto, Exception e) {
        if (TransactionStatus.WITHDRAW_SUCCESS.equals(tx.getStatus()) ||
                TransactionStatus.DEPOSIT_REQUESTED.equals(tx.getStatus())) {
            try {
                // 보상
                CommQueryPromise compPromise = commModule.sendPromiseQuery(CommunicationProtocol.REQUEST_COMPENSATE, TransferMapper.toCompensateRequest(tx));
                PromiseMessage msg = compPromise.get();
                tx.updateStatus(TransactionStatus.COMPENSATED);
                log.info("[보상 성공] 출금 복구 완료 - 계좌: {}", dto.getSendAccountNumber());

            } catch (Exception ex) {
                tx.updateStatus(TransactionStatus.COMPENSATION_FAILED);
                log.error("[보상 실패] 출금 복구 실패 - 계좌: {}", dto.getSendAccountNumber());

                // 보상 실패 → 비동기 알림 메시지 전송
                CompensateRequestDto compensateRequest = CompensateRequestDto.builder()
                        .transactionId(tx.getTransactionId().toString())
                        .build();

                commModule.sendMessage(CommunicationProtocol.REQUEST_COMPENSATE, compensateRequest, (res, ex2) -> {
                    if (ex2 != null) {
                        log.error("[보상 실패 알림 전송 실패]", ex2);
                    } else {
                        log.info("[보상 실패 알림 전송 완료]");
                    }
                });
            }
        } else {
            tx.updateStatus(TransactionStatus.FAILED);
        }
    }
}

