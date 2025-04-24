package kr.ssok.ssokopenbanking.account.service;

import kr.ssok.ssokopenbanking.global.client.BankServiceClient;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AccountServiceImpl implements AccountService {

    private final BankServiceClient bankServiceClient;


}
