package kr.ssok.ssokopenbanking.transfer.dto.request;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class CompensateRequestDto {
    private String transactionId;
}
