package kr.ssok.ssokopenbanking.global.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BankAccountReadRequestDto {
    private String username;
    private String phoneNumber;
}
