package kr.ssok.ssokopenbanking.account.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AccountInfoDto {
    private int bankCode;
    private String accountNumber;
    private int accountTypeCode;
}
