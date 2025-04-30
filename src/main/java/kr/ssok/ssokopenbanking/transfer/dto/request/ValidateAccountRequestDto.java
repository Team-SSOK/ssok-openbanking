package kr.ssok.ssokopenbanking.transfer.dto.request;

import lombok.*;

@Getter
@Builder
@NoArgsConstructor(force = true)
@AllArgsConstructor
public class ValidateAccountRequestDto {
    private String username; // 이름(실명)
    private String account;  // 계좌 번호
}
