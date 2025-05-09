package kr.ssok.ssokopenbanking.transfer.service;

import kr.ssok.ssokopenbanking.global.exception.TransferException;
import kr.ssok.ssokopenbanking.global.response.code.status.ErrorStatus;
import kr.ssok.ssokopenbanking.transfer.client.BankServiceClient;
import kr.ssok.ssokopenbanking.transfer.dto.request.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * 외부 은행 API와 통신하는 중간 서비스
 * - 계좌 유효성 검사, 휴면 여부 확인, 잔액 확인, 출금 및 입금, 보상 처리
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class BankApiService {

    private final BankServiceClient bankClient;

    // 계좌 유효성 검사
    public void validateAccount(String transactionId, ValidateAccountRequestDto requestDto) {
        try {
            var res = bankClient.validateAccount(requestDto);

            if (!res.isSuccess()) {
                log.error("[계좌 검증 실패] trxId: {}, 계좌번호: {}, 응답코드: {}, 메시지: {}", 
                          transactionId, requestDto.getAccount(), res.getCode(), res.getMessage());
                          
                throw new TransferException(
                        ErrorStatus.ACCOUNT_NOT_FOUND,
                        "유효하지 않은 계좌입니다: " + requestDto.getAccount(), 
                        transactionId
                );
            }
            
            log.info("[계좌 검증 성공] trxId: {}, 계좌번호: {}", transactionId, requestDto.getAccount());
        } catch (Exception e) {
            if (e instanceof TransferException) {
                throw e;
            }
            log.error("[계좌 검증 오류] trxId: {}, 계좌번호: {}, 오류: {}", 
                      transactionId, requestDto.getAccount(), e.getMessage(), e);
                      
            throw new TransferException(
                    ErrorStatus.ACCOUNT_NOT_FOUND,
                    "계좌 검증 중 오류가 발생했습니다: " + e.getMessage(),
                    transactionId
            );
        }
    }

    // 휴면 계좌 여부 확인
    public void checkDormant(String transactionId, CheckDormantRequestDto requestDto) {
        try {
            var res = bankClient.checkDormant(requestDto);

            // 조회 실패
            if (!res.isSuccess()) {
                log.error("[휴면계좌 조회 실패] trxId: {}, 계좌번호: {}, 응답코드: {}, 메시지: {}", 
                          transactionId, requestDto.getAccountNumber(), res.getCode(), res.getMessage());
                          
                throw new TransferException(
                        ErrorStatus.BAD_REQUEST, 
                        "계좌 조회 실패: " + requestDto.getAccountNumber(),
                        transactionId
                );
            }

            Map<String, Object> resultMap = res.getResult();
            Boolean isDormant = (Boolean) resultMap.get("isDormant");

            // 휴면 계좌
            if (Boolean.TRUE.equals(isDormant)) {
                log.warn("[휴면계좌 감지] trxId: {}, 계좌번호: {}", transactionId, requestDto.getAccountNumber());
                
                throw new TransferException(
                        ErrorStatus.ACCOUNT_DORMANT, 
                        "휴면 계좌입니다: " + requestDto.getAccountNumber(),
                        transactionId
                );
            }
            
            log.info("[휴면계좌 확인 성공] trxId: {}, 계좌번호: {}, 휴면상태: 정상", 
                     transactionId, requestDto.getAccountNumber());
        } catch (Exception e) {
            if (e instanceof TransferException) {
                throw e;
            }
            log.error("[휴면계좌 확인 오류] trxId: {}, 계좌번호: {}, 오류: {}", 
                      transactionId, requestDto.getAccountNumber(), e.getMessage(), e);
                      
            throw new TransferException(
                    ErrorStatus.BAD_REQUEST,
                    "휴면 계좌 확인 중 오류가 발생했습니다: " + e.getMessage(),
                    transactionId
            );
        }
    }

    // 잔액 확인
    public void checkBalance(String transactionId, CheckBalanceRequestDto requestDto) {
        try {
            var res = bankClient.checkBalance(requestDto);

            // 조회 실패
            if (!res.isSuccess()) {
                log.error("[잔액 조회 실패] trxId: {}, 계좌번호: {}, 응답코드: {}, 메시지: {}",
                        transactionId, requestDto.getAccount(), res.getCode(), res.getMessage());

                throw new TransferException(
                        ErrorStatus.BAD_REQUEST,
                        "잔액 조회 실패: " + requestDto.getAccount(),
                        transactionId
                );
            }

            Map<String, Object> resultMap = res.getResult();
            System.out.println(resultMap + "1");

            Object balanceObj = resultMap.get("balance");
            long balance = 0;

            // 숫자인 경우만 처리 (Integer, Long 등)
            if (balanceObj instanceof Number) {
                balance = ((Number) balanceObj).longValue();
            } else {
                log.error("[잔액 정보 오류] trxId: {}, 계좌번호: {}, balance 타입: {}",
                        transactionId, requestDto.getAccount(), balanceObj != null ? balanceObj.getClass() : "null");

                throw new TransferException(
                        ErrorStatus.BAD_REQUEST,
                        "잔액 정보가 올바르지 않습니다: " + requestDto.getAccount(),
                        transactionId
                );
            }

            boolean transferable = balance > 0;
            System.out.println(transferable + "1");

            // 잔액 부족
            if (!transferable) {
                log.warn("[잔액 부족] trxId: {}, 계좌번호: {}, 잔액: {}",
                        transactionId, requestDto.getAccount(), balance);

                throw new TransferException(
                        ErrorStatus.ACCOUNT_INSUFFICIENT_BALANCE,
                        "잔액이 부족합니다: " + requestDto.getAccount(),
                        transactionId
                );
            }

            log.info("[잔액 확인 성공] trxId: {}, 계좌번호: {}, 잔액: {}",
                    transactionId, requestDto.getAccount(), balance);

        } catch (Exception e) {
            if (e instanceof TransferException) {
                throw e;
            }

            log.error("[잔액 확인 오류] trxId: {}, 계좌번호: {}, 오류: {}",
                    transactionId, requestDto.getAccount(), e.getMessage(), e);

            throw new TransferException(
                    ErrorStatus.BAD_REQUEST,
                    "잔액 확인 중 오류가 발생했습니다: " + e.getMessage(),
                    transactionId
            );
        }
    }

    // 송금 가능 여부 검사
    public void checkTransferable(String transactionId, CheckTransferableRequestDto requestDto) {
        try {
            var res = bankClient.checkTransferable(requestDto);

            // API 응답 자체 실패
            if (!res.isSuccess()) {
                log.error("[송금 가능 여부 실패] trxId: {}, 계좌번호: {}, 응답코드: {}, 메시지: {}",
                        transactionId, requestDto.getAccount(), res.getCode(), res.getMessage());

                throw new TransferException(
                        ErrorStatus.TRANSFER_NOT_ALLOWED,
                        "송금 가능 여부 확인 실패: " + requestDto.getAccount(),
                        transactionId
                );
            }

            Map<String, Object> resultMap = res.getResult();
            Boolean transferable = (Boolean) resultMap.get("transferable");

            // transferable false 또는 null 일 때
            if (Boolean.FALSE.equals(transferable)) {
                log.warn("[송금 불가] trxId: {}, 계좌번호: {}", transactionId, requestDto.getAccount());

                throw new TransferException(
                        ErrorStatus.TRANSFER_NOT_ALLOWED,
                        "잔액 또는 출금 한도로 인해 송금이 불가능합니다: " + requestDto.getAccount(),
                        transactionId
                );
            }

            log.info("[송금 가능 확인 성공] trxId: {}, 계좌번호: {}", transactionId, requestDto.getAccount());

        } catch (Exception e) {
            if (e instanceof TransferException) {
                throw e;
            }
            // 그 외 예외처리
            log.error("[송금 가능 여부 검사 오류] trxId: {}, 계좌번호: {}, 오류: {}",
                    transactionId, requestDto.getAccount(), e.getMessage(), e);

            throw new TransferException(
                    ErrorStatus.TRANSFER_NOT_ALLOWED,
                    "송금 가능 여부 확인 중 오류가 발생했습니다: " + e.getMessage(),
                    transactionId
            );
        }
    }


    // 출금 요청
    public void withdraw(String transactionId, WithdrawRequestDto requestDto) {
        try {
            log.info("[출금 요청] trxId: {}, 계좌번호: {}, 금액: {}", 
                     transactionId, requestDto.getWithdrawAccount(), requestDto.getTransferAmount());
                     
            var res = bankClient.withdraw(requestDto);

            if (!res.isSuccess()) {
                log.error("[출금 실패] trxId: {}, 계좌번호: {}, 금액: {}, 응답코드: {}, 메시지: {}", 
                          transactionId, requestDto.getWithdrawAccount(), requestDto.getTransferAmount(),
                          res.getCode(), res.getMessage());
                          
                throw new TransferException(
                        ErrorStatus.WITHDRAW_FAILED,
                        "출금 실패: " + requestDto.getWithdrawAccount(),
                        transactionId
                );
            }
            
            log.info("[출금 성공] trxId: {}, 계좌번호: {}, 금액: {}", 
                     transactionId, requestDto.getWithdrawAccount(), requestDto.getTransferAmount());
        } catch (Exception e) {
            if (e instanceof TransferException) {
                throw e;
            }
            log.error("[출금 오류] trxId: {}, 계좌번호: {}, 오류: {}", 
                      transactionId, requestDto.getWithdrawAccount(), e.getMessage(), e);
                      
            throw new TransferException(
                    ErrorStatus.WITHDRAW_FAILED,
                    "출금 처리 중 오류가 발생했습니다: " + e.getMessage(),
                    transactionId
            );
        }
    }

    // 입금 요청
    public void deposit(String transactionId, DepositRequestDto requestDto) {
        try {
            log.info("[입금 요청] trxId: {}, 계좌번호: {}, 금액: {}", 
                     transactionId, requestDto.getDepositAccount(), requestDto.getTransferAmount());
                     
            var res = bankClient.deposit(requestDto);

            if (!res.isSuccess()) {
                log.error("[입금 실패] trxId: {}, 계좌번호: {}, 금액: {}, 응답코드: {}, 메시지: {}", 
                          transactionId, requestDto.getDepositAccount(), requestDto.getTransferAmount(),
                          res.getCode(), res.getMessage());
                          
                throw new TransferException(
                        ErrorStatus.DEPOSIT_FAILED,
                        "입금 실패: " + requestDto.getDepositAccount(),
                        transactionId
                );
            }
            
            log.info("[입금 성공] trxId: {}, 계좌번호: {}, 금액: {}", 
                     transactionId, requestDto.getDepositAccount(), requestDto.getTransferAmount());
        } catch (Exception e) {
            if (e instanceof TransferException) {
                throw e;
            }
            log.error("[입금 오류] trxId: {}, 계좌번호: {}, 오류: {}", 
                      transactionId, requestDto.getDepositAccount(), e.getMessage(), e);
                      
            throw new TransferException(
                    ErrorStatus.DEPOSIT_FAILED,
                    "입금 처리 중 오류가 발생했습니다: " + e.getMessage(),
                    transactionId
            );
        }
    }

    // 보상 요청 (출금 계좌로 복구 입금)
    public boolean compensate(String transactionId, DepositRequestDto requestDto) {
        try {
            log.info("[보상 입금 요청] trxId: {}, 계좌번호: {}, 금액: {}", 
                     transactionId, requestDto.getDepositAccount(), requestDto.getTransferAmount());
                     
            var res = bankClient.deposit(requestDto);
            
            if (!res.isSuccess()) {
                log.error("[보상 입금 실패] trxId: {}, 계좌번호: {}, 금액: {}, 응답코드: {}, 메시지: {}", 
                          transactionId, requestDto.getDepositAccount(), requestDto.getTransferAmount(),
                          res.getCode(), res.getMessage());
                return false;
            }
            
            log.info("[보상 입금 성공] trxId: {}, 계좌번호: {}, 금액: {}", 
                     transactionId, requestDto.getDepositAccount(), requestDto.getTransferAmount());
            return true;
        } catch (Exception e) {
            log.error("[보상 입금 오류] trxId: {}, 계좌번호: {}, 오류: {}", 
                      transactionId, requestDto.getDepositAccount(), e.getMessage(), e);
            return false;
        }
    }
}
