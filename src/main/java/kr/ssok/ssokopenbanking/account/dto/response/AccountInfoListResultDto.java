package kr.ssok.ssokopenbanking.account.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AccountInfoListResultDto {
    private List<AccountInfoDto> accounts;
}
