package kr.ssok.ssokopenbanking.transfer.service;

import kr.ssok.ssokopenbanking.transfer.client.BankServiceClient;
import kr.ssok.ssokopenbanking.global.exception.CustomException;
import kr.ssok.ssokopenbanking.global.response.code.status.ErrorStatus;
import kr.ssok.ssokopenbanking.transfer.dto.request.TransferRequestDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * 외부 은행 API와 통신하는 중간 서비스
 * - 계좌 유효성 검사, 휴면 여부 확인, 잔액 확인, 출금 및 입금, 보상 처리
 */
@Service
@RequiredArgsConstructor
public class BankApiService {

    private final BankServiceClient bankClient;

    // 계좌 유효성 검사
    public void validateAccount(String accountNumber, String bankCode) {
        var res = bankClient.validateAccount(accountNumber, bankCode);

        if (!res.isSuccess()) {
            throw new CustomException(ErrorStatus.ACCOUNT_NOT_FOUND, "유효하지 않은 계좌입니다: " + accountNumber);
        }
    }

    // 휴면 계좌 여부 확인
    public void checkDormant(String accountNumber, String bankCode) {
        var res = bankClient.checkDormant(accountNumber, bankCode);

        if (!res.isSuccess()) {
            throw new CustomException(ErrorStatus.BAD_REQUEST, "계좌 조회 실패: " + accountNumber);
        }

        Map<String, Object> resultMap = res.getResult();
        Boolean isDormant = (Boolean) resultMap.get("isDormant");

        if (Boolean.TRUE.equals(isDormant)) {
            throw new CustomException(ErrorStatus.ACCOUNT_DORMANT, "휴면 계좌입니다: " + accountNumber);
        }
    }

    // 잔액 확인
    public void checkBalance(String accountNumber, String bankCode, Long amount) {
        var res = bankClient.checkBalance(accountNumber, bankCode);

        if (!res.isSuccess()) {
            throw new CustomException(ErrorStatus.BAD_REQUEST, "잔액 조회 실패: " + accountNumber);
        }

        Map<String, Object> resultMap = res.getResult();

        Boolean transferable = (Boolean) resultMap.get("transferable");

        if (!Boolean.TRUE.equals(transferable)) {
            throw new CustomException(ErrorStatus.ACCOUNT_INSUFFICIENT_BALANCE,
                    "잔액이 부족합니다: " + accountNumber + ", 요청금액: " + amount);
        }
    }

    // 출금 요청
    public void withdraw(TransferRequestDto dto) {
        var res = bankClient.withdraw(Map.of(
                "withdrawAccount", dto.getSendAccountNumber(),
                "withdrawBankCode", dto.getSendBankCode(),
                "transferAmount", dto.getAmount(),
                "counterAccount", dto.getRecvAccountNumber(),
                "counterBankCode", dto.getRecvBankCode(),
                "currencyCode", 1
        ));

        if (!res.isSuccess()) {
            throw new CustomException(ErrorStatus.WITHDRAW_FAILED,
                    "출금 실패: " + dto.getSendAccountNumber() + ", 금액: " + dto.getAmount());
        }
    }

    // 입금 요청
    public void deposit(TransferRequestDto dto) {
        var res = bankClient.deposit(Map.of(
                "depositAccount", dto.getRecvAccountNumber(),
                "depositBankCode", dto.getRecvBankCode(),
                "transferAmount", dto.getAmount(),
                "counterAccount", dto.getSendAccountNumber(),
                "counterBankCode", dto.getSendBankCode(),
                "currencyCode", 1
        ));

        if (!res.isSuccess()) {
            throw new CustomException(ErrorStatus.DEPOSIT_FAILED,
                    "입금 실패: " + dto.getRecvAccountNumber() + ", 금액: " + dto.getAmount());
        }
    }

    // 보상 요청 (출금 계좌로 복구 입금)
    public boolean compensate(TransferRequestDto dto) {
        var res = bankClient.deposit(Map.of(
                "depositAccount", dto.getSendAccountNumber(),   // 출금했던 계좌로 다시 입금
                "depositBankCode", dto.getSendBankCode(),
                "transferAmount", dto.getAmount(),
                "counterAccount", dto.getRecvAccountNumber(),
                "counterBankCode", dto.getRecvBankCode(),
                "currencyCode", 1
        ));

        return res.isSuccess();
    }
}
