package kr.ssok.ssokopenbanking.transfer.service;

import kr.ssok.ssokopenbanking.global.comm.KafkaCommModule;
import kr.ssok.ssokopenbanking.global.comm.promise.CommQueryPromise;
import kr.ssok.ssokopenbanking.global.comm.promise.PromiseMessage;
import kr.ssok.ssokopenbanking.transfer.dto.request.*;
import kr.ssok.ssokopenbanking.transfer.dto.response.TransferResponseDto;
import kr.ssok.ssokopenbanking.transfer.entity.Transaction;
import kr.ssok.ssokopenbanking.transfer.enums.TransactionStatus;
import kr.ssok.ssokopenbanking.transfer.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static kr.ssok.ssokopenbanking.global.comm.CommunicationProtocol.*;
import static kr.ssok.ssokopenbanking.transfer.mapper.TransferMapper.*;

@Slf4j
@Service
@RequiredArgsConstructor
@Profile("kafka")
public class KafkaTransferServiceImpl implements TransferService {

    private final KafkaCommModule commModule;
    private final TransactionRepository transactionRepository;
    private final ExecutorService executor = Executors.newFixedThreadPool(2);

    @Transactional
    @Override
    public TransferResponseDto processTransfer(TransferRequestDto dto) {
        Transaction tx = Transaction.create(dto);
        tx.updateStatus(TransactionStatus.REQUESTED);
        transactionRepository.save(tx);

        try {
            String txId = tx.getTransactionId().toString();

            CompletableFuture<Void> validateSend = CompletableFuture.runAsync(() -> {
                validateAccountAndDormant(txId, dto.getSendName(), dto.getSendAccountNumber());
            }, executor);

            CompletableFuture<Void> validateRecv = CompletableFuture.runAsync(() -> {
                validateAccountAndDormant(txId, dto.getRevName(), dto.getRecvAccountNumber());
            }, executor);

            CompletableFuture.allOf(validateSend, validateRecv).join();
            tx.updateStatus(TransactionStatus.VALIDATED);

            CommQueryPromise balancePromise = commModule.sendPromiseQuery(CHECK_BALANCE,
                    CheckBalanceRequestDto.builder().account(dto.getSendAccountNumber()).build());
            balancePromise.get();

            tx.updateStatus(TransactionStatus.WITHDRAW_REQUESTED);
            CommQueryPromise withdrawPromise = commModule.sendPromiseQuery(REQUEST_WITHDRAW, toWithdrawRequest(tx));
            withdrawPromise.get();
            tx.updateStatus(TransactionStatus.WITHDRAW_SUCCESS);

            tx.updateStatus(TransactionStatus.DEPOSIT_REQUESTED);
            CommQueryPromise depositPromise = commModule.sendPromiseQuery(REQUEST_DEPOSIT, toDepositRequest(tx));
            depositPromise.get();
            tx.updateStatus(TransactionStatus.DEPOSIT_SUCCESS);

            tx.updateStatus(TransactionStatus.COMPLETED);
            return toResponse(tx, "송금이 성공적으로 처리되었습니다.");

        } catch (Exception e) {
            Throwable cause = (e instanceof CompletionException) ? e.getCause() : e;
            handleFailedTransaction(tx, dto, cause instanceof Exception ? (Exception) cause : new Exception(cause));
            throw new RuntimeException(cause);
        }
    }

    private void validateAccountAndDormant(String transactionId, String name, String accountNumber) {
        try {
            CommQueryPromise validatePromise = commModule.sendPromiseQuery(VALIDATE_ACCOUNT,
                    ValidateAccountRequestDto.builder().username(name).account(accountNumber).build());
            validatePromise.get();

            CommQueryPromise dormantPromise = commModule.sendPromiseQuery(CHECK_DORMANT,
                    CheckDormantRequestDto.builder().accountNumber(accountNumber).build());
            dormantPromise.get();

        } catch (Exception e) {
            throw new RuntimeException("계좌 유효성 또는 휴면 확인 실패", e);
        }
    }

    private void handleFailedTransaction(Transaction tx, TransferRequestDto dto, Exception e) {
        if (TransactionStatus.WITHDRAW_SUCCESS.equals(tx.getStatus()) ||
                TransactionStatus.DEPOSIT_REQUESTED.equals(tx.getStatus())) {

            try {
                CommQueryPromise compPromise = commModule.sendPromiseQuery(COMPENSATE_DEPOSIT, toDepositRequest(tx));
                PromiseMessage msg = compPromise.get();
                tx.updateStatus(TransactionStatus.COMPENSATED);
                log.info("[보상 성공] 출금 복구 완료 - 계좌: {}", dto.getSendAccountNumber());

            } catch (Exception ex) {
                tx.updateStatus(TransactionStatus.COMPENSATION_FAILED);
                log.error("[보상 실패] 출금 복구 실패 - 계좌: {}", dto.getSendAccountNumber());

                // 보상 실패 전송
                commModule.sendMessage(REQUEST_COMPENSATE,
                        CompensateRequestDto.builder()
                                .transactionId(tx.getTransactionId().toString())
                                .build(),
                        (res, ex2) -> {
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
