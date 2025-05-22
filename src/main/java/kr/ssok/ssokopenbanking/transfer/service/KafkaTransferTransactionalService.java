//package kr.ssok.ssokopenbanking.transfer.service;
//
//import jakarta.transaction.Transactional;
//import kr.ssok.ssokopenbanking.global.comm.CommunicationProtocol;
//import kr.ssok.ssokopenbanking.global.comm.KafkaCommModule;
//import kr.ssok.ssokopenbanking.global.comm.promise.CommQueryPromise;
//import kr.ssok.ssokopenbanking.global.comm.promise.PromiseMessage;
//import kr.ssok.ssokopenbanking.global.exception.TransferException;
//import kr.ssok.ssokopenbanking.transfer.dto.request.CheckTransferableRequestDto;
//import kr.ssok.ssokopenbanking.transfer.dto.request.CompensateRequestDto;
//import kr.ssok.ssokopenbanking.transfer.dto.request.TransferRequestDto;
//import kr.ssok.ssokopenbanking.transfer.dto.response.TransferResponseDto;
//import kr.ssok.ssokopenbanking.transfer.entity.Transaction;
//import kr.ssok.ssokopenbanking.transfer.enums.TransactionStatus;
//import kr.ssok.ssokopenbanking.transfer.mapper.TransferMapper;
//import kr.ssok.ssokopenbanking.transfer.repository.TransactionRepository;
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.stereotype.Service;
//
//import java.util.UUID;
//
//@Slf4j
//@Service
//@RequiredArgsConstructor
//public class KafkaTransferTransactionalService {
//
//    private final KafkaCommModule commModule;
//    private final TransactionRepository transactionRepository;
//    private final BankApiService bankApiService;
//
//    @Transactional
//    public TransferResponseDto completeTransferTransactional(TransferRequestDto dto, UUID txId) {
//        Transaction tx = Transaction.create(dto, txId);
//        tx.updateStatus(TransactionStatus.COMPLETED);
//        transactionRepository.save(tx);
//
//        try {
//            /*
//            // 잔액 및 송금 가능 여부 확인
//            bankApiService.checkBalance(txId.toString(), CheckBalanceRequestDto.builder()
//                    .account(dto.getSendAccountNumber()).build());
//            */
//
//            bankApiService.checkTransferable(txId.toString(), CheckTransferableRequestDto.builder()
//                    .username(dto.getSendName())
//                    .account(dto.getSendAccountNumber())
//                    .transferAmount(dto.getAmount()).build());
//
//            // tx.updateStatus(TransactionStatus.VALIDATED);
//
//            // 출금 요청
//            tx.updateStatus(TransactionStatus.WITHDRAW_REQUESTED);
//            commModule.sendPromiseQuery(dto.getSendAccountNumber(),
//                    CommunicationProtocol.REQUEST_WITHDRAW,
//                    TransferMapper.toWithdrawRequest(tx), 10).get();
//            tx.updateStatus(TransactionStatus.WITHDRAW_SUCCESS);
//
//            // 입금 요청
//            tx.updateStatus(TransactionStatus.DEPOSIT_REQUESTED);
//            commModule.sendPromiseQuery(dto.getRecvAccountNumber(),
//                    CommunicationProtocol.REQUEST_DEPOSIT,
//                    TransferMapper.toDepositRequest(tx), 10).get();
//            tx.updateStatus(TransactionStatus.DEPOSIT_SUCCESS);
//
//            // tx.updateStatus(TransactionStatus.COMPLETED);
//            return TransferMapper.toResponse(tx, "송금이 성공적으로 처리되었습니다.");
//
//        } catch (TransferException e) {
//            handleFailedTransaction(tx, dto, e);
//            throw e;
//
//        } catch (Exception e) {
//            handleFailedTransaction(tx, dto, e);
//            throw new RuntimeException("송금 처리 중 시스템 오류 발생", e);
//        }
//
//    }
//
//    private void handleFailedTransaction(Transaction tx, TransferRequestDto dto, Exception e) {
//        if (isAfterWithdrawButBeforeDeposit(tx)) {
//            try {
//                CommQueryPromise promise = commModule.sendPromiseQuery(
//                        CommunicationProtocol.REQUEST_COMPENSATE,
//                        TransferMapper.toCompensateRequest(tx)
//                );
//                PromiseMessage result = promise.get();
//                tx.updateStatus(TransactionStatus.COMPENSATED);
//                log.info("[보상 성공] 출금 복구 완료 - 계좌: {}", dto.getSendAccountNumber());
//            } catch (Exception ex) {
//                tx.updateStatus(TransactionStatus.COMPENSATION_FAILED);
//                log.error("[보상 실패] 출금 복구 실패 - 계좌: {}", dto.getSendAccountNumber());
//
//                // 비동기 알림
//                commModule.sendMessage(
//                        CommunicationProtocol.REQUEST_COMPENSATE,
//                        CompensateRequestDto.builder().transactionId(tx.getTransactionId().toString()).build(),
//                        (res, ex2) -> {
//                            if (ex2 != null) log.error("[보상 실패 알림 전송 실패]", ex2);
//                            else log.info("[보상 실패 알림 전송 완료]");
//                        }
//                );
//            }
//        } else {
//            tx.updateStatus(TransactionStatus.FAILED);
//        }
//    }
//
//    private static boolean isAfterWithdrawButBeforeDeposit(Transaction tx) {
//        return TransactionStatus.WITHDRAW_SUCCESS.equals(tx.getStatus()) ||
//                TransactionStatus.DEPOSIT_REQUESTED.equals(tx.getStatus());
//    }
//}
