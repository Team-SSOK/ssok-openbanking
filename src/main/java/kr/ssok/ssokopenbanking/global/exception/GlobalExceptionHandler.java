package kr.ssok.ssokopenbanking.global.exception;

import kr.ssok.ssokopenbanking.global.response.ApiResponse;
import kr.ssok.ssokopenbanking.global.response.code.status.ErrorStatus;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    // 커스텀 예외 처리
    @ExceptionHandler(CustomException.class)
    public ResponseEntity<ApiResponse<Object>> handleCustomException(CustomException ex) {
        log.error("CustomException 발생 - code: {}, message: {}", ex.getErrorCode().getReason().getCode(), ex.getMessage());

        // 클라이언트 응답은 "송금 처리 중 오류"로 통일
        return ApiResponse.error(ErrorStatus.TRANSFER_FAILED).toResponseEntity();
    }

    // 유효성 검증 실패 예외 처리
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Object>> handleValidationException(MethodArgumentNotValidException ex) {
        String errorMessage = ex.getBindingResult().getFieldErrors().stream()
                .map(fieldError -> fieldError.getField() + ": " + fieldError.getDefaultMessage())
                .findFirst()
                .orElse("유효성 검증에 실패했습니다.");
        
        return ApiResponse.error(ErrorStatus.VALIDATION_ERROR, errorMessage)
                .toResponseEntity(HttpStatus.BAD_REQUEST);
    }

    // IllegalArgumentException 처리
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiResponse<Object>> handleIllegalArgumentException(IllegalArgumentException ex) {
        return ApiResponse.error(ErrorStatus.BAD_REQUEST, ex.getMessage())
                .toResponseEntity(HttpStatus.BAD_REQUEST);
    }

    // NullPointerException 처리
    @ExceptionHandler(NullPointerException.class)
    public ResponseEntity<ApiResponse<Object>> handleNullPointerException(NullPointerException ex) {
        return ApiResponse.error(ErrorStatus.INTERNAL_SERVER_ERROR)
                .toResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR);
    }

    // 기타 모든 예외 처리
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Object>> handleGeneralException(Exception ex) {
        return ApiResponse.error(ErrorStatus.INTERNAL_SERVER_ERROR, ex.getMessage())
                .toResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
