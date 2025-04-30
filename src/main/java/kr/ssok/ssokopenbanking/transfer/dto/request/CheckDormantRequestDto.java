package kr.ssok.ssokopenbanking.transfer.dto.request;

import lombok.*;

@Getter
@Builder
@NoArgsConstructor(force = true)
@AllArgsConstructor
public class CheckDormantRequestDto {
    private String accountNumber; // 계좌번호
}
