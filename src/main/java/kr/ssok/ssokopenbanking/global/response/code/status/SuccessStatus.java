package kr.ssok.ssokopenbanking.global.response.code.status;

import kr.ssok.ssokopenbanking.global.response.code.ResponseCode;
import kr.ssok.ssokopenbanking.global.response.code.ResponseReason;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum SuccessStatus implements ResponseCode {

    // 일반 성공 응답
    OK(HttpStatus.OK, "200", "요청이 성공적으로 처리되었습니다."),
    TRANSFER_SUCCESS(HttpStatus.OK, "200", "송금에 성공했습니다.");

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;

    @Override
    public ResponseReason getReason() {
        return ResponseReason.builder()
                .httpStatus(httpStatus)
                .code(code)
                .message(message)
                .isSuccess(true)
                .build();
    }
}
