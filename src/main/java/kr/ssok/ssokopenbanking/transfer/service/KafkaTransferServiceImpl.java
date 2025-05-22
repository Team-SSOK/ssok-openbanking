//package kr.ssok.ssokopenbanking.transfer.service;
//
//import kr.ssok.ssokopenbanking.global.exception.TransferException;
//import kr.ssok.ssokopenbanking.transfer.dto.request.*;
//import kr.ssok.ssokopenbanking.transfer.dto.response.TransferResponseDto;
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.context.annotation.Profile;
//import org.springframework.stereotype.Service;
//
//import java.util.UUID;
//import java.util.concurrent.CompletableFuture;
//import java.util.concurrent.CompletionException;
//
//@Slf4j
//@Service
//@RequiredArgsConstructor
//@Profile("kafka")
//public class KafkaTransferServiceImpl implements TransferService {
//
//    private final BankApiService bankApiService;
//    private final KafkaTransferTransactionalService transactionalService;
//
//    @Override
//    public TransferResponseDto processTransfer(TransferRequestDto dto) {
//        UUID txId = UUID.randomUUID();
//
//        CompletableFuture<Void> validateSend = CompletableFuture.runAsync(() -> {
//            validateAccountAndDormant(txId.toString(), dto.getSendName(), dto.getSendAccountNumber());
//        });
//
//        CompletableFuture<Void> validateRecv = CompletableFuture.runAsync(() -> {
//            validateAccountAndDormant(txId.toString(), dto.getRecvName(), dto.getRecvAccountNumber());
//        });
//
//        try {
//            CompletableFuture.allOf(validateSend, validateRecv).join();
//        } catch (CompletionException e) {
//            throw (TransferException) e.getCause();
//        }
//
//        return transactionalService.completeTransferTransactional(dto, txId);
//    }
//
//    private void validateAccountAndDormant(String transactionId, String name, String accountNumber) {
//        bankApiService.validateAccount(
//                transactionId,
//                ValidateAccountRequestDto.builder()
//                        .username(name)
//                        .account(accountNumber)
//                        .build()
//        );
//
//        bankApiService.checkDormant(
//                transactionId,
//                CheckDormantRequestDto.builder()
//                        .accountNumber(accountNumber)
//                        .build()
//        );
//    }
//}
//
//
//
