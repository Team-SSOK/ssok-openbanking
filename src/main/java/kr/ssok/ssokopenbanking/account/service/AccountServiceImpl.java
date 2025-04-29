package kr.ssok.ssokopenbanking.account.service;

import kr.ssok.ssokopenbanking.account.client.BankAccountServiceClient;
import kr.ssok.ssokopenbanking.account.dto.request.AccountBalanceReadRequestDto;
import kr.ssok.ssokopenbanking.account.dto.request.AccountOwnerReadRequestDto;
import kr.ssok.ssokopenbanking.account.dto.request.AccountReadRequestDto;
import kr.ssok.ssokopenbanking.account.dto.response.AccountBalanceInfoResultDto;
import kr.ssok.ssokopenbanking.account.dto.response.AccountInfoDto;
import kr.ssok.ssokopenbanking.account.dto.response.AccountInfoListResultDto;
import kr.ssok.ssokopenbanking.account.dto.response.AccountOwnerInfoResultDto;
import kr.ssok.ssokopenbanking.account.dto.request.BankAccountBalanceReadRequestDto;
import kr.ssok.ssokopenbanking.account.dto.request.BankAccountOwnerReadRequestDto;
import kr.ssok.ssokopenbanking.account.dto.request.BankAccountReadRequestDto;
import kr.ssok.ssokopenbanking.account.dto.response.BankAccountBalanceInfoDto;
import kr.ssok.ssokopenbanking.account.dto.response.BankAccountInfoDto;
import kr.ssok.ssokopenbanking.account.dto.response.BankAccountOwnerInfoDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AccountServiceImpl implements AccountService {

    private final BankAccountServiceClient bankAccountServiceClient;

    /**
     * 은행별 계좌 조회
     * @param requestDto (이름, 전화번호)
     * @return 계좌 정보 리스트
     */
    @Override
    public AccountInfoListResultDto readAllAccounts(AccountReadRequestDto requestDto) {

        // 모든 은행(은행 DB에 있는 모든 은행 또는 FeignClient로 등록된 모든 은행 서비스)에 계좌 조회 요청 API를 보냄
        // 현재 은행이 쏙뱅크 하나밖에 없으므로, 단순히 쏙뱅크에만 요청을 보내는 로직으로 작성되어 있음
        BankAccountReadRequestDto bankRequestDto = BankAccountReadRequestDto.builder()
                .username(requestDto.getUsername())
                .phoneNumber(requestDto.getPhoneNumber())
                .build();
        List<BankAccountInfoDto> bankAccounts = bankAccountServiceClient.readAccount(bankRequestDto).getResult();

        // 뱅크 서비스에서 받은 응답으로부터 필요한 정보만 추출
        List<AccountInfoDto> accounts = bankAccounts.stream()
                .map(account -> AccountInfoDto.builder()
                        .bankCode(account.getBankCode())
                        .accountNumber(account.getAccountNumber())
                        .accountTypeCode(account.getAccountTypeCode())
                        .build())
                .toList();

        return AccountInfoListResultDto.builder()
                .accounts(accounts)
                .build();
    }

    /**
     * 계좌 잔액 조회
     * @param requestDto (계좌번호)
     * @return 잔액
     */
    @Override
    public AccountBalanceInfoResultDto readAccountBalance(AccountBalanceReadRequestDto requestDto) {

        // 지정된 은행에 요청을 보내기
        // 은행 지정 프로세스가 필요하나, 우선은 무조건 쏙뱅크로 요청을 보내는 로직으로 작성되어 있음
        BankAccountBalanceReadRequestDto bankRequestDto = BankAccountBalanceReadRequestDto.builder()
                .account(requestDto.getAccountNumber())
                .build();
        BankAccountBalanceInfoDto bankResultDto = bankAccountServiceClient.readAccountBalance(bankRequestDto).getResult();

        return AccountBalanceInfoResultDto.builder()
                .balance(bankResultDto.getBalance())
                .build();
    }


    /**
     * 계좌 실명 조회
     * @param requestDto (계좌번호)
     * @return 이름
     */
    @Override
    public AccountOwnerInfoResultDto readAccountOwner(AccountOwnerReadRequestDto requestDto) {

        // 지정된 은행에 요청을 보내기
        // 은행 지정 프로세스가 필요하나, 우선은 무조건 쏙뱅크로 요청을 보내는 로직으로 작성되어 있음
        BankAccountOwnerReadRequestDto bankRequestDto = BankAccountOwnerReadRequestDto.builder()
                .account(requestDto.getAccountNumber())
                .build();
        BankAccountOwnerInfoDto bankResultDto = bankAccountServiceClient.readAccountOwner(bankRequestDto).getResult();

        return AccountOwnerInfoResultDto.builder()
                .username(bankResultDto.getUsername())
                .build();
    }


}
