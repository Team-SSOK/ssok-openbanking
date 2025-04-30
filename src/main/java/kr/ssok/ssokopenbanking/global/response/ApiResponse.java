package kr.ssok.ssokopenbanking.global.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import kr.ssok.ssokopenbanking.global.exception.CustomException;
import kr.ssok.ssokopenbanking.global.exception.TransferException;
import kr.ssok.ssokopenbanking.global.response.code.ResponseCode;
import kr.ssok.ssokopenbanking.global.response.code.ResponseReason;
import kr.ssok.ssokopenbanking.global.response.code.status.SuccessStatus;
import kr.ssok.ssokopenbanking.transfer.dto.response.TransferResponseDto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.UUID;

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

    // AccountController 호환성을 위한 메서드
    public static <T> ApiResponse<T> onSuccess(T result) {
        return success(result);
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

    // 실패 응답 생성 - 코드와 결과를 함께 반환
    public static <T> ApiResponse<T> error(ResponseCode code, T result) {
        ResponseReason reason = code.getReason();
        return new ApiResponse<>(reason.getIsSuccess(), reason.getCode(), reason.getMessage(), result);
    }

    // 실패 응답 생성 - 예외로부터 응답 생성
    public static <T> ApiResponse<T> error(CustomException exception) {
        if (exception.getErrorCode() != null) {
            return error(exception.getErrorCode(), exception.getMessage());
        } else {
            return new ApiResponse<>(false, "UNKNOWN", exception.getMessage(), null);
        }
    }

    // 송금 관련 예외 처리 전용 메서드
    public static ApiResponse<TransferResponseDto> transferError(TransferException exception) {
        ResponseReason reason = exception.getErrorCode().getReason();
        
        // 실패 응답 DTO 생성
        TransferResponseDto errorResult = TransferResponseDto.builder()
                .transactionId(exception.getTransactionId())
                .status("FAILED")
                .message(exception.getMessage()) // 구체적인 오류 메시지는 여기에 포함
                .build();
        
        // 상위 메시지는 항상 "송금에 실패했습니다."로 통일
        return new ApiResponse<>(false, reason.getCode(), "송금에 실패했습니다.", errorResult);
    }
    
    // 송금 실패 응답 생성 - 트랜잭션 ID가 없는 경우
    public static ApiResponse<TransferResponseDto> transferError(ResponseCode code, String errorMessage) {
        ResponseReason reason = code.getReason();
        
        // 실패 응답 DTO 생성 (임의의 트랜잭션 ID 생성)
        TransferResponseDto errorResult = TransferResponseDto.builder()
                .transactionId(UUID.randomUUID().toString())
                .status("FAILED")
                .message(errorMessage) // 구체적인 오류 메시지는 여기에 포함
                .build();
        
        // 상위 메시지는 항상 "송금에 실패했습니다."로 통일
        return new ApiResponse<>(false, reason.getCode(), "송금에 실패했습니다.", errorResult);
    }
    
    // 송금 실패 응답 생성 - 결과 객체를 직접 전달하는 경우
    public static ApiResponse<TransferResponseDto> transferError(ResponseCode code, TransferResponseDto result) {
        ResponseReason reason = code.getReason();
        return new ApiResponse<>(false, reason.getCode(), "송금에 실패했습니다.", result);
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
