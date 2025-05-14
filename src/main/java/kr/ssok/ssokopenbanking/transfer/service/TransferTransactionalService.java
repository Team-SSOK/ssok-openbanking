package kr.ssok.ssokopenbanking.transfer.service;

import jakarta.transaction.Transactional;
import kr.ssok.ssokopenbanking.global.exception.TransferException;
import kr.ssok.ssokopenbanking.transfer.dto.request.*;
import kr.ssok.ssokopenbanking.transfer.dto.response.TransferResponseDto;
import kr.ssok.ssokopenbanking.transfer.entity.Transaction;
import kr.ssok.ssokopenbanking.transfer.enums.TransactionStatus;
import kr.ssok.ssokopenbanking.transfer.mapper.TransferMapper;
import kr.ssok.ssokopenbanking.transfer.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class TransferTransactionalService {

    private final TransactionRepository transactionRepository;
    private final BankApiService bankApiService;

    @Transactional
    public TransferResponseDto completeTransferTransactional(TransferRequestDto dto, UUID txId) {
        Transaction tx = Transaction.create(dto, txId);
        tx.updateStatus(TransactionStatus.REQUESTED);
        transactionRepository.save(tx);

        try {
            /*
            bankApiService.checkBalance(txId.toString(), CheckBalanceRequestDto.builder()
                    .account(dto.getSendAccountNumber())
                    .build());
            */

            bankApiService.checkTransferable(txId.toString(), CheckTransferableRequestDto.builder()
                    .username(dto.getSendName())
                    .account(dto.getSendAccountNumber())
                    .transferAmount(dto.getAmount())
                    .build());

            tx.updateStatus(TransactionStatus.VALIDATED);

            tx.updateStatus(TransactionStatus.WITHDRAW_REQUESTED);
            bankApiService.withdraw(txId.toString(), TransferMapper.toWithdrawRequest(tx));
            tx.updateStatus(TransactionStatus.WITHDRAW_SUCCESS);

            tx.updateStatus(TransactionStatus.DEPOSIT_REQUESTED);
            bankApiService.deposit(txId.toString(), TransferMapper.toDepositRequest(tx));
            tx.updateStatus(TransactionStatus.DEPOSIT_SUCCESS);

            tx.updateStatus(TransactionStatus.COMPLETED);
            return TransferMapper.toResponse(tx, "송금이 성공적으로 처리되었습니다.");

        } catch (TransferException e) {
            handleFailedTransaction(tx, dto, e);
            throw e;

        } catch (Exception e) {
            handleFailedTransaction(tx, dto, e);
            throw new RuntimeException("송금 처리 중 시스템 오류 발생", e);
        }
    }

    private void handleFailedTransaction(Transaction tx, TransferRequestDto dto, Exception e) {
        if (isAfterWithdrawButBeforeDeposit(tx)) {

            boolean compensated = bankApiService.compensate(
                    tx.getTransactionId().toString(),
                    TransferMapper.toDepositRequest(tx)
            );

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

    private static boolean isAfterWithdrawButBeforeDeposit(Transaction tx) {
        return TransactionStatus.WITHDRAW_SUCCESS.equals(tx.getStatus()) ||
                TransactionStatus.DEPOSIT_REQUESTED.equals(tx.getStatus());
    }
}
