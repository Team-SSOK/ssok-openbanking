package kr.ssok.ssokopenbanking.transfer.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

// 은행 API 응답 결과
@Getter
@Builder
@AllArgsConstructor
public class BankApiResponseDto<T> {
    private boolean isSuccess;
    private String code;
    private String message;
    private T result;
}