package kr.ssok.ssokopenbanking.transfer.client;

import kr.ssok.ssokopenbanking.transfer.dto.response.BankApiResponseDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Map;

// @FeignClient(name = "bankServiceClient", url = "${bank.api.url}")
@FeignClient(name="bank-service")
public interface BankServiceClient {

    // 계좌 유효성 검사
    @GetMapping("/api/bank/account/valid")
    BankApiResponseDto<Map<String, Object>> validateAccount(@RequestParam String accountNumber, @RequestParam String bankCode);

    // 휴면 계좌 여부 검사
    @GetMapping("/api/bank/account/dormant")
    BankApiResponseDto<Map<String, Object>> checkDormant(@RequestParam String accountNumber, @RequestParam String bankCode);

    // 계좌 잔액 조회
    @GetMapping("/api/bank/account/balance")
    BankApiResponseDto<Map<String, Object>> checkBalance(@RequestParam String accountNumber, @RequestParam String bankCode);

    // 출금 이체
    @PostMapping("/api/bank/transfer/withdraw")
    BankApiResponseDto<Map<String, Object>> withdraw(@RequestBody Map<String, Object> request);

    // 입금 이체
    @PostMapping("/api/bank/transfer/deposit")
    BankApiResponseDto<Map<String, Object>> deposit(@RequestBody Map<String, Object> request);
}
