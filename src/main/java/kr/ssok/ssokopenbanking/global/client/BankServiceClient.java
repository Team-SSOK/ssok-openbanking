package kr.ssok.ssokopenbanking.global.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name="bank-service")
public interface BankServiceClient {

    // 계좌 조회 API
    @GetMapping("/api/bank/account")
    ResponseEntity<BankAccountReadResponseDto> readAccount(@RequestBody BankAccountReadRequestDto requestDto);

    // 계좌 실명 조회 API (예금주명 조회)
    @GetMapping("/api/bank/account/owner")
    ResponseEntity<BankAccountOwnerReadResponseDto> readAccountOwner(@RequestBody BankAccountOwnerReadRequestDto requestDto);

    // 계좌 유효성 검사 API
    @GetMapping("/api/bank/account/valid")
    ResponseEntity<BankAccountValidCheckResponseDto> chekAccountValid(@RequestBody BankAccountValidCheckRequestDto requestDto);

    // 휴면 계좌 여부 검사 API
    @GetMapping("/api/bank/account/dormant")
    ResponseEntity<BankAccountDormantCheckResponseDto> checkAccountDormant(@RequestBody BankAccountDormantCheckRequestDto requestDto);

    // 계좌 잔액 조회 API (잔액 조건 검사)
    @GetMapping("/api/bank/account/balance")
    ResponseEntity<BankAccountBalnaceCheckResponseDto> checkAccountBalance(@RequestBody BankAccountCheckBalanceRequestDto requestDto);

    // 출금 이체 API
    @PostMapping("/api/bank/transfer/withdraw")
    ResponseEntity<BankTransferWithDrawResponseDto> withdraw(@RequestBody BankTransferWithDrawRequestDto requestDto);

    // 입금 이체 API
    @PostMapping("/api/bank/transfer/deposit")
    ResponseEntity<BankTransferDepositResponseDto> deposit(@RequestBody BankTransferDepositRequestDto requestDto);

}
