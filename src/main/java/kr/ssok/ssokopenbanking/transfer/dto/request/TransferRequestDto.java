package kr.ssok.ssokopenbanking.transfer.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

// 송금 요청
@Getter
@Builder
@NoArgsConstructor(force = true)
@AllArgsConstructor
public class TransferRequestDto {
    private final String sendAccountNumber;
    private final String sendBankCode;
    private final String recvAccountNumber;
    private final String recvBankCode;
    private final Long amount;
}