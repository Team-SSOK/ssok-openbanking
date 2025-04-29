package kr.ssok.ssokopenbanking.transfer.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

// 송금 요청 처리 결과
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TransferResponseDto {
    private String transactionId;
    private String status;
    private String message;
}
