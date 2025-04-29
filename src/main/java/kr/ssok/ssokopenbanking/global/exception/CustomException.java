package kr.ssok.ssokopenbanking.global.exception;

import kr.ssok.ssokopenbanking.global.response.code.ResponseCode;
import kr.ssok.ssokopenbanking.global.response.code.ResponseReason;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class CustomException extends RuntimeException {

    private final ResponseCode errorCode;
    private final HttpStatus httpStatus;


    // ResponseCode 기반 생성자
    public CustomException(ResponseCode errorCode) {
        super(errorCode.getReason().getMessage());
        this.errorCode = errorCode;
        this.httpStatus = errorCode.getReason().getHttpStatus();
    }

    // ResponseCode + 커스텀 메시지 생성자
    public CustomException(ResponseCode errorCode, String customMessage) {
        super(customMessage);
        this.errorCode = errorCode;
        this.httpStatus = errorCode.getReason().getHttpStatus();
    }

    // 직접 메시지 + 상태 지정 생성자
    public CustomException(String message, int status) {
        super(message);
        this.errorCode = null;
        this.httpStatus = HttpStatus.valueOf(status);
    }

    // HTTP 상태 코드 리턴
    public int getStatus() {
        return httpStatus != null ? httpStatus.value() : 500;
    }

    // 에러 Reason 리턴
    public ResponseReason getErrorReason() {
        if (errorCode != null) {
            return errorCode.getReason();
        }
        return ResponseReason.builder()
                .httpStatus(httpStatus != null ? httpStatus : HttpStatus.INTERNAL_SERVER_ERROR)
                .code("UNKNOWN")
                .message(super.getMessage())
                .isSuccess(false)
                .build();
    }
}

