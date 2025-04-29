package kr.ssok.ssokopenbanking.account.client;

import kr.ssok.ssokopenbanking.account.dto.request.BankAccountBalanceReadRequestDto;
import kr.ssok.ssokopenbanking.account.dto.request.BankAccountOwnerReadRequestDto;
import kr.ssok.ssokopenbanking.account.dto.request.BankAccountReadRequestDto;
import kr.ssok.ssokopenbanking.account.dto.response.BankAccountBalanceInfoDto;
import kr.ssok.ssokopenbanking.account.dto.response.BankAccountInfoDto;
import kr.ssok.ssokopenbanking.account.dto.response.BankAccountOwnerInfoDto;
import kr.ssok.ssokopenbanking.global.response.ApiResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@FeignClient(name="bank-account-service")
public interface BankAccountServiceClient {

    // 계좌 조회 API
    @GetMapping("/api/bank/account")
    ApiResponse<List<BankAccountInfoDto>> readAccount(@RequestBody BankAccountReadRequestDto requestDto);

    // 계좌 잔액 조회 API
    @GetMapping("/api/bank/account/balance")
    ApiResponse<BankAccountBalanceInfoDto> readAccountBalance(@RequestBody BankAccountBalanceReadRequestDto requestDto);

    // 계좌 실명 조회 API (예금주명 조회)
    @GetMapping("/api/bank/account/owner")
    ApiResponse<BankAccountOwnerInfoDto> readAccountOwner(@RequestBody BankAccountOwnerReadRequestDto requestDto);

}
