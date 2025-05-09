package kr.ssok.ssokopenbanking.transfer.mapper;


import kr.ssok.ssokopenbanking.transfer.dto.request.CompensateRequestDto;
import kr.ssok.ssokopenbanking.transfer.dto.request.DepositRequestDto;
import kr.ssok.ssokopenbanking.transfer.dto.request.WithdrawRequestDto;
import kr.ssok.ssokopenbanking.transfer.dto.response.TransferResponseDto;
import kr.ssok.ssokopenbanking.transfer.entity.Transaction;

// Transfer 관련 매퍼 클래스
public class TransferMapper {

    // Transaction → 출금 요청 DTO 변환
    public static WithdrawRequestDto toWithdrawRequest(Transaction tx) {
        return WithdrawRequestDto.builder()
                .transactionId(tx.getTransactionId().toString())
                .withdrawBankCode(tx.getSendBankCode())
                .withdrawAccount(tx.getSendAccount())
                .transferAmount(tx.getAmount())
                .currencyCode(1)
                .counterAccount(tx.getRecvAccount())
                .counterBankCode(tx.getRecvBankCode())
                .build();
    }

    // Transaction → 입금 요청 DTO 변환
    public static DepositRequestDto toDepositRequest(Transaction tx) {
        return DepositRequestDto.builder()
                .transactionId(tx.getTransactionId().toString())
                .depositBankCode(tx.getRecvBankCode())
                .depositAccount(tx.getRecvAccount())
                .transferAmount(tx.getAmount())
                .currencyCode(1)
                .counterAccount(tx.getSendAccount())
                .counterBankCode(tx.getSendBankCode())
                .build();
    }

    // Transaction → 최종 응답 DTO 변환
    public static TransferResponseDto toResponse(Transaction tx, String message) {
        return TransferResponseDto.builder()
                .transactionId(tx.getTransactionId().toString())
                .status(tx.getStatus().name())
                .message(message)
                .build();
    }

    // 보상 요청용 DTO 매핑
    public static CompensateRequestDto toCompensateRequest(Transaction tx) {
        return CompensateRequestDto.builder()
                .transactionId(tx.getTransactionId().toString())
                .build();
    }
}
