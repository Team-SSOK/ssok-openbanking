package kr.ssok.ssokopenbanking.account.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AccountOwnerReadRequestDto {
    private String accountNumber;
    private int bankCode;
}
