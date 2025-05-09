package kr.ssok.ssokopenbanking.transfer.dto.request;

import lombok.*;

@Getter
@Builder
@NoArgsConstructor(force = true)
@AllArgsConstructor
public class CheckTransferableRequestDto {
    private String username;
    private String account;
    private Long transferAmount;
}
