package kr.ssok.ssokopenbanking.transfer.service;

import feign.FeignException;
import kr.ssok.ssokopenbanking.global.exception.TransferException;
import kr.ssok.ssokopenbanking.global.response.code.status.ErrorStatus;
import kr.ssok.ssokopenbanking.transfer.client.BankServiceClient;
import kr.ssok.ssokopenbanking.transfer.dto.request.*;
import kr.ssok.ssokopenbanking.transfer.dto.response.BankApiResponseDto;
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

            if (isFailedResponse(res)) {
                log.error("[계좌 검증 실패] trxId: {}, 계좌번호: {}, 응답코드: {}, 메시지: {}",
                        transactionId, requestDto.getAccount(), res.getCode(), res.getMessage());

                throw new TransferException(
                        ErrorStatus.ACCOUNT_NOT_FOUND,
                        "유효하지 않은 계좌입니다: " + requestDto.getAccount(),
                        transactionId
                );
            }

            log.info("[계좌 검증 성공] trxId: {}, 계좌번호: {}", transactionId, requestDto.getAccount());

        } catch (TransferException e) {
            throw e;

        } catch (FeignException e) {
            log.error("[계좌 검증 API 호출 실패] trxId: {}, 계좌번호: {}, 오류: {}",
                    transactionId, requestDto.getAccount(), e.getMessage(), e);

            throw new TransferException(
                    ErrorStatus.BAD_GATEWAY,
                    "은행 API 호출 실패: " + e.getMessage(),
                    transactionId
            );

        } catch (Exception e) {
            log.error("[계좌 검증 알 수 없는 오류] trxId: {}, 계좌번호: {}, 오류: {}",
                    transactionId, requestDto.getAccount(), e.getMessage(), e);

            throw new TransferException(
                    ErrorStatus.INTERNAL_SERVER_ERROR,
                    "계좌 검증 중 예기치 못한 오류가 발생했습니다.",
                    transactionId
            );
        }
    }

    private static boolean isFailedResponse(BankApiResponseDto<Map<String, Object>> res) {
        return !res.isSuccess();
    }


    // 휴면 계좌 여부 확인
    public void checkDormant(String transactionId, CheckDormantRequestDto requestDto) {
        try {
            var res = bankClient.checkDormant(requestDto);

            if (isFailedResponse(res)) {
                log.error("[휴면계좌 조회 실패] trxId: {}, 계좌번호: {}, 응답코드: {}, 메시지: {}",
                        transactionId, requestDto.getAccountNumber(), res.getCode(), res.getMessage());

                throw new TransferException(
                        ErrorStatus.ACCOUNT_READ_FAILED, // 명세 통일 = COMMON400 수정
                        "계좌 조회 실패: " + requestDto.getAccountNumber(),
                        transactionId
                );
            }

            Map<String, Object> resultMap = res.getResult();
            Boolean isDormant = (Boolean) resultMap.get("isDormant");
            boolean isDormantAccount = Boolean.TRUE.equals(isDormant);

            if (isDormantAccount) {
                log.warn("[휴면계좌 감지] trxId: {}, 계좌번호: {}", transactionId, requestDto.getAccountNumber());

                throw new TransferException(
                        ErrorStatus.ACCOUNT_DORMANT, // 명세 통일 = COMMON400 수정
                        "휴면 계좌입니다: " + requestDto.getAccountNumber(),
                        transactionId
                );
            }

            log.info("[휴면계좌 확인 성공] trxId: {}, 계좌번호: {}, 휴면상태: 정상",
                    transactionId, requestDto.getAccountNumber());

        } catch (TransferException e) {
            throw e;

        } catch (FeignException e) {
            log.error("[휴면계좌 API 호출 실패] trxId: {}, 계좌번호: {}, 오류: {}",
                    transactionId, requestDto.getAccountNumber(), e.getMessage(), e);

            throw new TransferException(
                    ErrorStatus.BAD_GATEWAY,
                    "은행 API 호출 실패: " + e.getMessage(),
                    transactionId
            );

        } catch (Exception e) {
            log.error("[휴면계좌 확인 알 수 없는 오류] trxId: {}, 계좌번호: {}, 오류: {}",
                    transactionId, requestDto.getAccountNumber(), e.getMessage(), e);

            throw new TransferException(
                    ErrorStatus.INTERNAL_SERVER_ERROR,
                    "휴면 계좌 확인 중 예기치 못한 오류가 발생했습니다.",
                    transactionId
            );
        }
    }


    /*
    // 잔액 확인
    public void checkBalance(String transactionId, CheckBalanceRequestDto requestDto) {
        try {
            var res = bankClient.checkBalance(requestDto);

            if (isFailedResponse(res)) {
                log.error("[잔액 조회 실패] trxId: {}, 계좌번호: {}, 응답코드: {}, 메시지: {}",
                        transactionId, requestDto.getAccount(), res.getCode(), res.getMessage());

                throw new TransferException(
                        ErrorStatus.ACCOUNT_BALANCE_READ_FAILED, // 명세 통일 = ACCOUNT003 수정
                        "잔액 조회 실패: " + requestDto.getAccount(),
                        transactionId
                );
            }

            Map<String, Object> resultMap = res.getResult();
            Object balanceObj = resultMap.get("balance");
            long balance;

            // 숫자인 경우만 처리
            if (balanceObj instanceof Number) {
                balance = ((Number) balanceObj).longValue();
            } else {
                log.error("[잔액 정보 오류] trxId: {}, 계좌번호: {}, balance 타입: {}",
                        transactionId, requestDto.getAccount(), balanceObj != null ? balanceObj.getClass() : "null");

                throw new TransferException(
                        ErrorStatus.INTERNAL_SERVER_ERROR,
                        "잔액 정보가 올바르지 않습니다: " + requestDto.getAccount(),
                        transactionId
                );
            }

            if (balance <= 0) {
                log.warn("[잔액 부족] trxId: {}, 계좌번호: {}, 잔액: {}",
                        transactionId, requestDto.getAccount(), balance);

                throw new TransferException(
                        ErrorStatus.ACCOUNT_INSUFFICIENT_BALANCE, // 명세 통일 = ACCOUNT003 수정
                        "잔액이 부족합니다: " + requestDto.getAccount(),
                        transactionId
                );
            }

            log.info("[잔액 확인 성공] trxId: {}, 계좌번호: {}, 잔액: {}",
                    transactionId, requestDto.getAccount(), balance);

        } catch (TransferException e) {
            throw e;

        } catch (FeignException e) {
            log.error("[잔액 조회 API 호출 실패] trxId: {}, 계좌번호: {}, 오류: {}",
                    transactionId, requestDto.getAccount(), e.getMessage(), e);

            throw new TransferException(
                    ErrorStatus.BAD_GATEWAY,
                    "은행 API 호출 실패: " + e.getMessage(),
                    transactionId
            );

        } catch (Exception e) {
            log.error("[잔액 확인 알 수 없는 오류] trxId: {}, 계좌번호: {}, 오류: {}",
                    transactionId, requestDto.getAccount(), e.getMessage(), e);

            throw new TransferException(
                    ErrorStatus.INTERNAL_SERVER_ERROR,
                    "잔액 확인 중 예기치 못한 오류가 발생했습니다.",
                    transactionId
            );
        }
    }
    */

    // 송금 가능 여부 검사
    public void checkTransferable(String transactionId, CheckTransferableRequestDto requestDto) {
        try {
            var res = bankClient.checkTransferable(requestDto);

            if (isFailedResponse(res)) {
                String code = res.getCode();
                String message = res.getMessage();
                Map<String, Object> resultMap = res.getResult();

                log.warn("[송금 불가] trxId: {}, 계좌번호: {}, 응답코드: {}, 메시지: {}",
                        transactionId, requestDto.getAccount(), code, message);

                // result null
                if (resultMap == null) {
                    // 계좌 없을 때
                    if (code.equals("ACCOUNT4001")) {
                        throw new TransferException(
                                ErrorStatus.ACCOUNT_NOT_FOUND,
                                "계좌를 찾을 수 없습니다: " + requestDto.getAccount(),
                                transactionId
                        );
                    }
                    throw new TransferException(
                            ErrorStatus.TRANSFER_NOT_ALLOWED,
                            "송금 실패: " + message,
                            transactionId
                    );
                }

                // result 있는 경우 -> transferable 값으로 분기
                Boolean transferable = (Boolean) resultMap.get("transferable");
                boolean isNotTransferable = Boolean.FALSE.equals(transferable);

                if (isNotTransferable) {
                    switch (code) {
                        case "TRANSFER4004": // 잔액 부족
                            throw new TransferException(
                                    ErrorStatus.TRANSFER_NOT_ALLOWED,
                                    "해당 계좌는 잔액이 부족합니다: " + requestDto.getAccount(),
                                    transactionId
                            );
                        case "ACCOUNT4009": // 출금 한도 도달
                            throw new TransferException(
                                    ErrorStatus.TRANSFER_NOT_ALLOWED,
                                    "해당 계좌의 출금 한도에 도달하였습니다: " + requestDto.getAccount(),
                                    transactionId
                            );
                        default:
                            throw new TransferException(
                                    ErrorStatus.TRANSFER_NOT_ALLOWED,
                                    "송금 실패: " + message,
                                    transactionId
                            );
                    }
                }
            }

            log.info("[송금 가능 확인 성공] trxId: {}, 계좌번호: {}", transactionId, requestDto.getAccount());

        } catch (TransferException e) {
            throw e;

        } catch (FeignException e) {
            log.error("[송금 가능 여부 API 호출 실패] trxId: {}, 계좌번호: {}, 오류: {}",
                    transactionId, requestDto.getAccount(), e.getMessage(), e);

            throw new TransferException(
                    ErrorStatus.BAD_GATEWAY,
                    "은행 API 호출 실패: " + e.getMessage(),
                    transactionId
            );

        } catch (Exception e) {
            log.error("[송금 가능 여부 확인 중 알 수 없는 오류] trxId: {}, 계좌번호: {}, 오류: {}",
                    transactionId, requestDto.getAccount(), e.getMessage(), e);

            throw new TransferException(
                    ErrorStatus.INTERNAL_SERVER_ERROR,
                    "송금 가능 여부 확인 중 알 수 없는 오류가 발생했습니다.",
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

            if (isFailedResponse(res)) {
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

        } catch (TransferException e) {
            throw e;

        } catch (FeignException e) {
            log.error("[출금 API 호출 실패] trxId: {}, 계좌번호: {}, 오류: {}",
                    transactionId, requestDto.getWithdrawAccount(), e.getMessage(), e);

            throw new TransferException(
                    ErrorStatus.BAD_GATEWAY,
                    "은행 API 호출 실패: " + e.getMessage(),
                    transactionId
            );

        } catch (Exception e) {
            log.error("[출금 처리 중 알 수 없는 오류] trxId: {}, 계좌번호: {}, 오류: {}",
                    transactionId, requestDto.getWithdrawAccount(), e.getMessage(), e);

            throw new TransferException(
                    ErrorStatus.INTERNAL_SERVER_ERROR,
                    "출금 처리 중 예기치 못한 오류가 발생했습니다.",
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

            if (isFailedResponse(res)) {
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

        } catch (TransferException e) {
            throw e;

        } catch (FeignException e) {
            log.error("[입금 API 호출 실패] trxId: {}, 계좌번호: {}, 오류: {}",
                    transactionId, requestDto.getDepositAccount(), e.getMessage(), e);

            throw new TransferException(
                    ErrorStatus.BAD_GATEWAY,
                    "은행 API 호출 실패: " + e.getMessage(),
                    transactionId
            );

        } catch (Exception e) {
            log.error("[입금 처리 중 알 수 없는 오류] trxId: {}, 계좌번호: {}, 오류: {}",
                    transactionId, requestDto.getDepositAccount(), e.getMessage(), e);

            throw new TransferException(
                    ErrorStatus.INTERNAL_SERVER_ERROR,
                    "입금 처리 중 예기치 못한 오류가 발생했습니다.",
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

            if (isFailedResponse(res)) {
                log.error("[보상 입금 실패] trxId: {}, 계좌번호: {}, 금액: {}, 응답코드: {}, 메시지: {}",
                        transactionId, requestDto.getDepositAccount(), requestDto.getTransferAmount(),
                        res.getCode(), res.getMessage());
                return false;
            }

            log.info("[보상 입금 성공] trxId: {}, 계좌번호: {}, 금액: {}",
                    transactionId, requestDto.getDepositAccount(), requestDto.getTransferAmount());
            return true;

        } catch (FeignException e) {
            log.error("[보상 입금 API 호출 실패] trxId: {}, 계좌번호: {}, 오류: {}",
                    transactionId, requestDto.getDepositAccount(), e.getMessage(), e);
            return false;

        } catch (Exception e) {
            log.error("[보상 입금 시스템 오류] trxId: {}, 계좌번호: {}, 오류: {}",
                    transactionId, requestDto.getDepositAccount(), e.getMessage(), e);
            return false;
        }
    }
}
