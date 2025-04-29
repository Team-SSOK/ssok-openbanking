package kr.ssok.ssokopenbanking.global.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BankAccountBalanceInfoDto {
    private long balance;
}
