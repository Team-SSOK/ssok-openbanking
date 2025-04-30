package kr.ssok.ssokopenbanking.global.exception;

import kr.ssok.ssokopenbanking.global.response.ApiResponse;
import kr.ssok.ssokopenbanking.global.response.code.status.ErrorStatus;
import kr.ssok.ssokopenbanking.transfer.dto.response.TransferResponseDto;
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

    // 송금 관련 예외 처리
    @ExceptionHandler(TransferException.class)
    public ResponseEntity<ApiResponse<TransferResponseDto>> handleTransferException(TransferException ex) {
        log.error("[TransferException] transactionId: {}, code: {}, message: {}", 
                  ex.getTransactionId(), 
                  ex.getErrorCode().getReason().getCode(), 
                  ex.getMessage());
        
        HttpStatus status = ex.getHttpStatus() != null ? ex.getHttpStatus() : HttpStatus.INTERNAL_SERVER_ERROR;
        ApiResponse<TransferResponseDto> response = ApiResponse.transferError(ex);
        return new ResponseEntity<>(response, status);
    }

    // 일반 커스텀 예외 처리
    @ExceptionHandler(CustomException.class)
    public ResponseEntity<ApiResponse<Object>> handleCustomException(CustomException ex) {
        log.error("[CustomException] code: {}, message: {}", 
                  ex.getErrorCode() != null ? ex.getErrorCode().getReason().getCode() : "UNKNOWN", 
                  ex.getMessage());
        
        HttpStatus status = ex.getHttpStatus() != null ? ex.getHttpStatus() : HttpStatus.INTERNAL_SERVER_ERROR;
        ApiResponse<Object> response = ApiResponse.error(ex);
        return new ResponseEntity<>(response, status);
    }

    /*
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
    */

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
