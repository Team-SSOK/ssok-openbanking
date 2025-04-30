package kr.ssok.ssokopenbanking.transfer.client;

import kr.ssok.ssokopenbanking.transfer.dto.request.*;
import kr.ssok.ssokopenbanking.transfer.dto.response.BankApiResponseDto;
import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * 외부 은행 API 호출 실패 시 대체 응답을 제공하는 Fallback Factory
 * - 개발 및 테스트 환경에서 활용
 */
@Component
public class BankServiceClientFallbackFactory implements FallbackFactory<BankServiceClient> {
    
    @Override
    public BankServiceClient create(Throwable cause) {
        return new BankServiceClient() {
            @Override
            public BankApiResponseDto<Map<String, Object>> validateAccount(ValidateAccountRequestDto requestDto) {
                Map<String, Object> result = new HashMap<>();
                result.put("valid", true);
                
                return BankApiResponseDto.<Map<String, Object>>builder()
                        .isSuccess(true)
                        .code("200")
                        .message("계좌 검증 성공(Mock)")
                        .result(result)
                        .build();
            }

            @Override
            public BankApiResponseDto<Map<String, Object>> checkDormant(CheckDormantRequestDto requestDto) {
                Map<String, Object> result = new HashMap<>();
                result.put("isDormant", false);
                
                return BankApiResponseDto.<Map<String, Object>>builder()
                        .isSuccess(true)
                        .code("200")
                        .message("휴면계좌 확인 성공(Mock)")
                        .result(result)
                        .build();
            }

            @Override
            public BankApiResponseDto<Map<String, Object>> checkBalance(CheckBalanceRequestDto requestDto) {
                Map<String, Object> result = new HashMap<>();
                result.put("transferable", true);
                result.put("balance", 1000000L);
                
                return BankApiResponseDto.<Map<String, Object>>builder()
                        .isSuccess(true)
                        .code("200")
                        .message("잔액 확인 성공(Mock)")
                        .result(result)
                        .build();
            }

            @Override
            public BankApiResponseDto<Map<String, Object>> withdraw(WithdrawRequestDto requestDto) {
                Map<String, Object> result = new HashMap<>();
                result.put("transactionId", requestDto.getTransactionId());
                result.put("withdrawAccount", requestDto.getWithdrawAccount());
                
                return BankApiResponseDto.<Map<String, Object>>builder()
                        .isSuccess(true)
                        .code("200")
                        .message("출금 성공(Mock)")
                        .result(result)
                        .build();
            }

            @Override
            public BankApiResponseDto<Map<String, Object>> deposit(DepositRequestDto requestDto) {
                Map<String, Object> result = new HashMap<>();
                result.put("transactionId", requestDto.getTransactionId());
                result.put("depositAccount", requestDto.getDepositAccount());
                
                return BankApiResponseDto.<Map<String, Object>>builder()
                        .isSuccess(true)
                        .code("200")
                        .message("입금 성공(Mock)")
                        .result(result)
                        .build();
            }
        };
    }
}
