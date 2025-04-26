package kr.ssok.ssokopenbanking.account.service;

import kr.ssok.ssokopenbanking.account.dto.request.AccountBalanceReadRequestDto;
import kr.ssok.ssokopenbanking.account.dto.request.AccountOwnerReadRequestDto;
import kr.ssok.ssokopenbanking.account.dto.request.AccountReadRequestDto;
import kr.ssok.ssokopenbanking.account.dto.response.AccountBalanceInfoResultDto;
import kr.ssok.ssokopenbanking.account.dto.response.AccountInfoListResultDto;
import kr.ssok.ssokopenbanking.account.dto.response.AccountOwnerInfoResultDto;

public interface AccountService {

    AccountInfoListResultDto readAllAccounts(AccountReadRequestDto requestDto);
    AccountBalanceInfoResultDto readAccountBalance(AccountBalanceReadRequestDto requestDto);
    AccountOwnerInfoResultDto readAccountOwner(AccountOwnerReadRequestDto requestDto);

}
