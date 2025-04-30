package kr.ssok.ssokopenbanking.global.response.code;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Builder;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@Builder
public class ResponseReason {
    private final HttpStatus httpStatus;
    private final boolean isSuccess;
    private final String code;
    private final String message;

    // 기존 메서드를 유지하되 Jackson이 직렬화하지 않도록 설정
    @JsonIgnore
    public boolean getIsSuccess() {
        return isSuccess;
    }
    
    // 표준 Boolean getter도 추가
    public boolean isSuccess() {
        return isSuccess;
    }
}
