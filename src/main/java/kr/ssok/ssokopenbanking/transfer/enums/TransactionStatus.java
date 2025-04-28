package kr.ssok.ssokopenbanking.transfer.enums;

public enum TransactionStatus {
    REQUESTED,             // 송금 요청 시작
    VALIDATED,             // 계좌 유효성 및 휴면 여부 검증 완료
    WITHDRAW_REQUESTED,    // 출금 요청 시작
    WITHDRAW_SUCCESS,      // 출금 성공
    DEPOSIT_REQUESTED,     // 입금 요청 시작
    DEPOSIT_SUCCESS,       // 입금 성공
    DEPOSIT_FAILED,        // 입금 실패
    COMPENSATED,           // 보상 입금 성공
    COMPENSATION_FAILED,   // 보상 입금 실패
    FAILED,                // 기타 실패
    COMPLETED              // 완료
}
