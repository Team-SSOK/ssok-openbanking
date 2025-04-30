package kr.ssok.ssokopenbanking.account.controller;

import kr.ssok.ssokopenbanking.account.dto.request.AccountBalanceReadRequestDto;
import kr.ssok.ssokopenbanking.account.dto.request.AccountOwnerReadRequestDto;
import kr.ssok.ssokopenbanking.account.dto.request.AccountReadRequestDto;
import kr.ssok.ssokopenbanking.account.dto.response.AccountBalanceInfoResultDto;
import kr.ssok.ssokopenbanking.account.dto.response.AccountInfoDto;
import kr.ssok.ssokopenbanking.account.dto.response.AccountInfoListResultDto;
import kr.ssok.ssokopenbanking.account.dto.response.AccountOwnerInfoResultDto;
import kr.ssok.ssokopenbanking.account.service.AccountServiceImpl;
import kr.ssok.ssokopenbanking.global.response.ApiResponse;
import kr.ssok.ssokopenbanking.global.response.code.status.ErrorStatus;
import kr.ssok.ssokopenbanking.global.response.code.status.SuccessStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/openbank")
public class AccountController {

    private final AccountServiceImpl accountServiceImpl;

    /**
     * 은행별 계좌 조회 요청 API
     */
    @PostMapping("/accounts/request")
    public ResponseEntity<ApiResponse<List<AccountInfoDto>>> readAllAccounts(@RequestBody AccountReadRequestDto requestDto) {

        List<AccountInfoDto> accountInfos = accountServiceImpl.readAllAccounts(requestDto);

        if (accountInfos != null) {
            return ApiResponse.success(SuccessStatus.ACCOUNT_READ_SUCCESS, accountInfos).toResponseEntity();
        } else {
            return ApiResponse.<List<AccountInfoDto>>error(ErrorStatus.ACCOUNT_READ_FAILED).toResponseEntity();
        }
    }

    /**
     * 계좌 잔액 조회 요청 API
     */
    @PostMapping("/account/balance")
    public ResponseEntity<ApiResponse<AccountBalanceInfoResultDto>> readBalance(@RequestBody AccountBalanceReadRequestDto requestDto) {

        AccountBalanceInfoResultDto accountBalanceInfo = accountServiceImpl.readAccountBalance(requestDto);

        if (accountBalanceInfo != null) {
            return ApiResponse.success(SuccessStatus.ACCOUNT_BALANCE_READ_SUCCESS, accountBalanceInfo).toResponseEntity();
        } else {
            return ApiResponse.<AccountBalanceInfoResultDto>error(ErrorStatus.ACCOUNT_BALANCE_READ_FAILED).toResponseEntity();
        }
    }

    /**
     * 계좌 실명 조회 요청 API
     */
    @PostMapping("/account/verify-name")
    public ResponseEntity<ApiResponse<AccountOwnerInfoResultDto>> readAccountOwner(@RequestBody AccountOwnerReadRequestDto requestDto) {

        AccountOwnerInfoResultDto accountOwnerInfo = accountServiceImpl.readAccountOwner(requestDto);

        if (accountOwnerInfo != null) {
            return ApiResponse.success(SuccessStatus.ACCOUNT_OWNER_READ_SUCCESS, accountOwnerInfo).toResponseEntity();
        } else {
            return ApiResponse.<AccountOwnerInfoResultDto>error(ErrorStatus.ACCOUNT_OWNER_READ_FAILED).toResponseEntity();
        }
    }

}
