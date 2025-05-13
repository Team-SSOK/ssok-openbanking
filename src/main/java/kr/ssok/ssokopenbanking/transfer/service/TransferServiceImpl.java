package kr.ssok.ssokopenbanking.transfer.service;

import kr.ssok.ssokopenbanking.global.exception.TransferException;
import kr.ssok.ssokopenbanking.transfer.dto.request.CheckDormantRequestDto;
import kr.ssok.ssokopenbanking.transfer.dto.request.TransferRequestDto;
import kr.ssok.ssokopenbanking.transfer.dto.request.ValidateAccountRequestDto;
import kr.ssok.ssokopenbanking.transfer.dto.response.TransferResponseDto;
import kr.ssok.ssokopenbanking.transfer.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;

@Service
@RequiredArgsConstructor
@Slf4j
@Profile("default")
public class TransferServiceImpl implements TransferService {

    private final BankApiService bankApiService;
    private final TransferTransactionalService transactionalService;
    private final TransactionRepository transactionRepository;

    @Override
    public TransferResponseDto processTransfer(TransferRequestDto dto) {
        UUID txId = UUID.randomUUID();

        CompletableFuture<Void> validateSendAccount = CompletableFuture.runAsync(() -> {
            validateAccountAndDormant(txId.toString(), dto.getSendName(), dto.getSendAccountNumber());
        });

        CompletableFuture<Void> validateRecvAccount = CompletableFuture.runAsync(() -> {
            validateAccountAndDormant(txId.toString(), dto.getRecvName(), dto.getRecvAccountNumber());
        });

        try {
            CompletableFuture.allOf(validateSendAccount, validateRecvAccount).join();
        } catch (CompletionException e) {
            throw (TransferException) e.getCause();
        }

        return transactionalService.completeTransferTransactional(dto, txId);
    }

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
}
