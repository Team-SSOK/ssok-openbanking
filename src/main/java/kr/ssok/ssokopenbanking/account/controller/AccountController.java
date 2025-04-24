package kr.ssok.ssokopenbanking.account.controller;

import kr.ssok.ssokopenbanking.account.service.AccountServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/openbank")
public class AccountController {

    private final AccountServiceImpl accountServiceImpl;


}
