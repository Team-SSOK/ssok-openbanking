package kr.ssok.ssokopenbanking.account.controller;

import kr.ssok.ssokopenbanking.account.dto.request.AccountBalanceReadRequestDto;
import kr.ssok.ssokopenbanking.account.dto.request.AccountOwnerReadRequestDto;
import kr.ssok.ssokopenbanking.account.dto.request.AccountReadRequestDto;
import kr.ssok.ssokopenbanking.account.dto.response.AccountBalanceInfoResultDto;
import kr.ssok.ssokopenbanking.account.dto.response.AccountInfoListResultDto;
import kr.ssok.ssokopenbanking.account.dto.response.AccountOwnerInfoResultDto;
import kr.ssok.ssokopenbanking.account.service.AccountServiceImpl;
import kr.ssok.ssokopenbanking.global.apiPayload.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/openbank")
public class AccountController {

    private final AccountServiceImpl accountServiceImpl;

    /**
     * 은행별 계좌 조회 요청 API
     */
    @PostMapping("/accounts/request")
    public ApiResponse<AccountInfoListResultDto> readAllAccounts(AccountReadRequestDto requestDto) {

        AccountInfoListResultDto accountInfos = accountServiceImpl.readAllAccounts(requestDto);

        return ApiResponse.onSuccess(accountInfos);
    }

    /**
     * 계좌 잔액 조회 요청 API
     */
    @GetMapping("/account/balance")
    public ApiResponse<AccountBalanceInfoResultDto> readBalance(AccountBalanceReadRequestDto requestDto) {

        AccountBalanceInfoResultDto accountBalanceInfo = accountServiceImpl.readAccountBalance(requestDto);

        return ApiResponse.onSuccess(accountBalanceInfo);
    }

    /**
     * 계좌 실명 조회 요청 API
     */
    @PostMapping("/account/verify-name")
    public ApiResponse<AccountOwnerInfoResultDto> readAccountOwner(AccountOwnerReadRequestDto requestDto) {

        AccountOwnerInfoResultDto accountOwnerInfo = accountServiceImpl.readAccountOwner(requestDto);

        return ApiResponse.onSuccess(accountOwnerInfo);
    }

}
