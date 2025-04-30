package kr.ssok.ssokopenbanking.transfer.dto.request;

import lombok.*;

@Getter
@Builder
@NoArgsConstructor(force = true)
@AllArgsConstructor
public class WithdrawRequestDto {

    // 거래 식별자 (UUID 등 고유값)
    private final String transactionId;

    // 출금 은행 코드
    private final String withdrawBankCode;

    // 출금 계좌 번호
    private final String withdrawAccount;

    // 이체 금액
    private final Long transferAmount;

    // 통화 코드
    private final Long currencyCode;

    // 수취인 계좌 번호
    private final String counterAccount;

    // 수취인 은행 코드
    private final String counterBankCode;
}
