package kr.ssok.ssokopenbanking.transfer.client;

import kr.ssok.ssokopenbanking.transfer.dto.request.*;
import kr.ssok.ssokopenbanking.transfer.dto.response.BankApiResponseDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Map;

/**
 * 외부 은행 API를 호출하기 위한 Feign Client 인터페이스
 */
@FeignClient(name="bank-service", url="${bank.api.url}")
public interface BankServiceClient {

    // 계좌 유효성 검사
    @PostMapping("/api/bank/account/valid")
    BankApiResponseDto<Map<String, Object>> validateAccount(@RequestBody ValidateAccountRequestDto requestDto);

    // 휴면 계좌 여부 검사
    @PostMapping("/api/bank/account/dormant")
    BankApiResponseDto<Map<String, Object>> checkDormant(@RequestBody CheckDormantRequestDto requestDto);

    // 계좌 잔액 조회
    @PostMapping("/api/bank/account/balance")
    BankApiResponseDto<Map<String, Object>> checkBalance(@RequestBody CheckBalanceRequestDto requestDto);

    // 출금 이체
    @PostMapping("/api/bank/transfer/withdraw")
    BankApiResponseDto<Map<String, Object>> withdraw(@RequestBody WithdrawRequestDto requestDto);

    // 입금 이체
    @PostMapping("/api/bank/transfer/deposit")
    BankApiResponseDto<Map<String, Object>> deposit(@RequestBody DepositRequestDto requestDto);
}