package kr.ssok.ssokopenbanking.transfer.client;

import kr.ssok.ssokopenbanking.transfer.dto.request.*;
import kr.ssok.ssokopenbanking.transfer.dto.response.BankApiResponseDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

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

    // 송금 한도 검사
    @PostMapping("api/bank/account/transferable")
    BankApiResponseDto<Map<String, Object>> checkTransferable(@RequestBody CheckTransferableRequestDto requestDto);

    // 출금 이체
    @PostMapping("/api/bank/transfer/withdraw")
    BankApiResponseDto<Map<String, Object>> withdraw(@RequestBody WithdrawRequestDto requestDto);

    // 입금 이체
    @PostMapping("/api/bank/transfer/deposit")
    BankApiResponseDto<Map<String, Object>> deposit(@RequestBody DepositRequestDto requestDto);

    // 보상
    @PostMapping("/api/bank/transfer/compensate")
    BankApiResponseDto<Map<String, Object>> compensate(@RequestBody CompensateRequestDto requestDto);
}