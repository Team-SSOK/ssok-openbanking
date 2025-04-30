package kr.ssok.ssokopenbanking.transfer.dto.request;

import lombok.*;

@Getter
@Builder
@NoArgsConstructor(force = true)
@AllArgsConstructor
public class DepositRequestDto {

    // 거래 식별자
    private String transactionId;

    // 입금 받을 은행 코드
    private int depositBankCode;

    // 입금 받을 계좌 번호
    private String depositAccount;

    // 입금 금액
    private Long transferAmount;

    // 통화 코드
    private int currencyCode;

    // 송금인의 계좌 번호 (반대편 계좌)
    private String counterAccount;

    // 송금인의 은행 코드 (반대편 은행)
    private int counterBankCode;
}
