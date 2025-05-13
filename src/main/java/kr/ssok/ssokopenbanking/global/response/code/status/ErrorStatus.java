package kr.ssok.ssokopenbanking.global.response.code.status;

import kr.ssok.ssokopenbanking.global.response.code.ResponseCode;
import kr.ssok.ssokopenbanking.global.response.code.ResponseReason;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ErrorStatus implements ResponseCode {

    // 400 BAD_REQUEST 오류
    BAD_REQUEST(HttpStatus.BAD_REQUEST, "COMMON400", "잘못된 요청입니다."),
    VALIDATION_ERROR(HttpStatus.BAD_REQUEST, "VALIDATION_ERROR", "유효성 검증에 실패했습니다."),
    INVALID_PARAMETER(HttpStatus.BAD_REQUEST, "INVALID_PARAMETER", "유효하지 않은 파라미터입니다."),
    
    // 401 권한 관련 오류
    UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "COMMON401", "인증이 필요합니다."),
    
    // 403 권한 관련 오류
    FORBIDDEN(HttpStatus.FORBIDDEN, "COMMON403", "접근 권한이 없습니다."),
    
    // 404 NOT_FOUND 오류
    NOT_FOUND(HttpStatus.NOT_FOUND, "NOT_FOUND", "리소스를 찾을 수 없습니다."),
    
    // 405 Method Not Allowed
    METHOD_NOT_ALLOWED(HttpStatus.METHOD_NOT_ALLOWED, "METHOD_NOT_ALLOWED", "지원하지 않는 HTTP 메서드입니다."),
    
    // 409 Conflict
    CONFLICT(HttpStatus.CONFLICT, "CONFLICT", "리소스 충돌이 발생했습니다."),
    
    // 500 서버 오류
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "COMMON500", "서버 내부 오류가 발생했습니다."),
    
    // 502 Bad Gateway
    BAD_GATEWAY(HttpStatus.BAD_GATEWAY, "BAD_GATEWAY", "게이트웨이 오류가 발생했습니다."),
    
    // 503 Service Unavailable
    SERVICE_UNAVAILABLE(HttpStatus.SERVICE_UNAVAILABLE, "SERVICE_UNAVAILABLE", "서비스를 일시적으로 사용할 수 없습니다."),
    
    // 계좌 오류
    ACCOUNT_NOT_FOUND(HttpStatus.NOT_FOUND, "ACCOUNT001", "유효하지 않은 계좌입니다."),
    ACCOUNT_DORMANT(HttpStatus.FORBIDDEN, "COMMON400", "휴면 계좌입니다."),
    ACCOUNT_INSUFFICIENT_BALANCE(HttpStatus.BAD_REQUEST, "ACCOUNT003", "잔액이 부족합니다."),

    // 송금 오류
    TRANSFER_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "TRANSFER001", "송금 처리 중 오류가 발생했습니다."),
    WITHDRAW_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "TRANSFER002", "출금 처리 중 오류가 발생했습니다."),
    DEPOSIT_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "TRANSFER003", "입금 처리 중 오류가 발생했습니다."),
    TRANSFER_NOT_ALLOWED(HttpStatus.BAD_REQUEST, "TRANSFER004", "잔액 또는 출금 한도로 인해 송금이 불가능합니다."),

    // 조회 오류
    ACCOUNT_READ_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "COMMON400", "계좌 목록 조회 중 오류가 발생했습니다."),
    ACCOUNT_BALANCE_READ_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "ACCOUNT003", "계좌 잔액 조회 중 오류가 발생했습니다."),
    ACCOUNT_OWNER_READ_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "READ003", "계좌 실명 조회 중 오류가 발생했습니다.");

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;

    @Override
    public ResponseReason getReason() {
        return ResponseReason.builder()
                .httpStatus(httpStatus)
                .code(code)
                .message(message)
                .isSuccess(false)
                .build();
    }
}
