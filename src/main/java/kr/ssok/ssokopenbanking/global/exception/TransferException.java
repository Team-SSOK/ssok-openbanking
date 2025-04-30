package kr.ssok.ssokopenbanking.global.exception;

import kr.ssok.ssokopenbanking.global.response.code.ResponseCode;
import lombok.Getter;

/**
 * 송금 관련 예외 처리를 위한 전용 예외 클래스
 * 일반 CustomException에 트랜잭션 ID를 추가로 포함
 */
@Getter
public class TransferException extends CustomException {
    private final String transactionId;
    
    public TransferException(ResponseCode errorCode, String message, String transactionId) {
        super(errorCode, message);
        this.transactionId = transactionId;
    }
    
    public TransferException(ResponseCode errorCode, String transactionId) {
        super(errorCode);
        this.transactionId = transactionId;
    }
}
