package kr.ssok.ssokopenbanking.global.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import kr.ssok.ssokopenbanking.global.exception.CustomException;
import kr.ssok.ssokopenbanking.global.response.code.ResponseCode;
import kr.ssok.ssokopenbanking.global.response.code.ResponseReason;
import kr.ssok.ssokopenbanking.global.response.code.status.SuccessStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@Getter
@AllArgsConstructor
@JsonPropertyOrder({"isSuccess", "code", "message", "result"})
public class ApiResponse<T> {

    @JsonProperty("isSuccess")
    private final boolean isSuccess;
    private final String code;
    private final String message;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private final T result;

    // 성공 응답 생성 - 코드와 결과를 함께 반환
    public static <T> ApiResponse<T> success(ResponseCode code, T result) {
        ResponseReason reason = code.getReason();
        return new ApiResponse<>(reason.getIsSuccess(), reason.getCode(), reason.getMessage(), result);
    }

    // 성공 응답 생성 - 결과만 전달 (기본 SuccessStatus.OK 사용)
    public static <T> ApiResponse<T> success(T result) {
        return success(SuccessStatus.OK, result);
    }

    // 실패 응답 생성 - 코드만 전달
    public static <T> ApiResponse<T> error(ResponseCode code) {
        ResponseReason reason = code.getReason();
        return new ApiResponse<>(reason.getIsSuccess(), reason.getCode(), reason.getMessage(), null);
    }

    // 실패 응답 생성 - 코드와 커스텀 메시지
    public static <T> ApiResponse<T> error(ResponseCode code, String customMessage) {
        ResponseReason reason = code.getReason();
        return new ApiResponse<>(reason.getIsSuccess(), reason.getCode(), customMessage, null);
    }

    // 실패 응답 생성 - 예외로부터 응답 생성
    public static <T> ApiResponse<T> error(CustomException exception) {
        if (exception.getErrorCode() != null) {
            return error(exception.getErrorCode(), exception.getMessage());
        } else {
            return new ApiResponse<>(false, "UNKNOWN", exception.getMessage(), null);
        }
    }

    // ResponseEntity로 변환
    public ResponseEntity<ApiResponse<T>> toResponseEntity() {
        return ResponseEntity.status(isSuccess ? HttpStatus.OK : HttpStatus.INTERNAL_SERVER_ERROR).body(this);
    }

    // ResponseEntity로 변환 - 상태 코드 지정
    public ResponseEntity<ApiResponse<T>> toResponseEntity(HttpStatus status) {
        return new ResponseEntity<>(this, status);
    }
}
