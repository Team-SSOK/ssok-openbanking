package kr.ssok.ssokopenbanking.global.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BankAccountInfoDto {
    private String accountNumber;
    private long balance;
    private int bankCode;
    private int accountStatusCode;
    private int accountTypeCode;
    private long withdrawLimit;
    private LocalDate createdAt;
    private LocalDate updatedAt;
}
