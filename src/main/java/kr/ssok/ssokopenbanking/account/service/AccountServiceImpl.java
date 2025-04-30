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
import kr.ssok.ssokopenbanking.global.exception.CustomException;
import kr.ssok.ssokopenbanking.global.response.code.status.ErrorStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class AccountServiceImpl implements AccountService {

    private final BankAccountServiceClient bankAccountServiceClient;

    /**
     * 은행별 계좌 조회
     * @param requestDto (이름, 전화번호)
     * @return 계좌 정보 리스트
     */
    @Override
    public List<AccountInfoDto> readAllAccounts(AccountReadRequestDto requestDto) {

        // 요청
        BankAccountReadRequestDto bankRequestDto = BankAccountReadRequestDto.builder()
                .username(requestDto.getUsername())
                .phoneNumber(requestDto.getPhoneNumber())
                .build();

        try {
            // 응답
            var res = bankAccountServiceClient.readAccount(bankRequestDto);
            if (!res.isSuccess()) {
                throw new CustomException(ErrorStatus.BAD_REQUEST, res.getMessage());
            }
            // 결과
            List<BankAccountInfoDto> bankAccounts = res.getResult();
            // 뱅크 서비스에서 받은 응답으로부터 필요한 정보만 추출
            List<AccountInfoDto> accounts = bankAccounts.stream()
                    .map(account -> AccountInfoDto.builder()
                            .bankCode(account.getBankCode())
                            .accountNumber(account.getAccountNumber())
                            .accountTypeCode(account.getAccountTypeCode())
                            .build())
                    .toList();
            /*
            AccountInfoListResultDto resultDto = AccountInfoListResultDto.builder()
                    .accounts(accounts)
                    .build();
             */
            return accounts;
        } catch (CustomException e) {
            log.error("[계좌 목록 조회 실패] 비즈니스 예외 발생 - 사유: {}", e.getMessage());
            return null;
        } catch (Exception e) {
            log.error("[계좌 목록 조회 실패] 시스템 예외 발생 - 사유: {}", e.getMessage(), e);
            return null;
        }

    }

    /**
     * 계좌 잔액 조회
     * @param requestDto (계좌번호)
     * @return 잔액
     */
    @Override
    public AccountBalanceInfoResultDto readAccountBalance(AccountBalanceReadRequestDto requestDto) {

        // 요청
        BankAccountBalanceReadRequestDto bankRequestDto = BankAccountBalanceReadRequestDto.builder()
                .account(requestDto.getAccountNumber())
                .build();

        try {
            // 응답
            var res = bankAccountServiceClient.readAccountBalance(bankRequestDto);
            if (!res.isSuccess()) {
                throw new CustomException(ErrorStatus.BAD_REQUEST, res.getMessage());
            }
            // 결과
            BankAccountBalanceInfoDto bankResultDto = res.getResult();
            AccountBalanceInfoResultDto resultDto = AccountBalanceInfoResultDto.builder()
                    .balance(bankResultDto.getBalance())
                    .build();
            return resultDto;
        } catch (CustomException e) {
            log.error("[계좌 잔액 조회 실패] 비즈니스 예외 발생 - 사유: {}", e.getMessage());
            return null;
        } catch (Exception e) {
            log.error("[계좌 잔액 조회 실패] 시스템 예외 발생 - 사유: {}", e.getMessage(), e);
            return null;
        }
    }


    /**
     * 계좌 실명 조회
     * @param requestDto (계좌번호)
     * @return 이름
     */
    @Override
    public AccountOwnerInfoResultDto readAccountOwner(AccountOwnerReadRequestDto requestDto) {

        // 요청
        BankAccountOwnerReadRequestDto bankRequestDto = BankAccountOwnerReadRequestDto.builder()
                .account(requestDto.getAccountNumber())
                .build();

        try {
            // 응답
            var res = bankAccountServiceClient.readAccountOwner(bankRequestDto);
            if (!res.isSuccess()) {
                throw new CustomException(ErrorStatus.BAD_REQUEST, res.getMessage());
            }
            // 결과
            BankAccountOwnerInfoDto bankResultDto = res.getResult();
            AccountOwnerInfoResultDto resultDto = AccountOwnerInfoResultDto.builder()
                    .username(bankResultDto.getUsername())
                    .build();
            return resultDto;
        } catch (CustomException e) {
            log.error("[계좌 실명 조회 실패] 비즈니스 예외 발생 - 사유: {}", e.getMessage());
            return null;
        } catch (Exception e) {
            log.error("[계좌 실명 조회 실패] 시스템 예외 발생 - 사유: {}", e.getMessage(), e);
            return null;
        }

    }

}
