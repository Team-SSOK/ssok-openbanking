package kr.ssok.ssokopenbanking.global.comm;

public class CommunicationProtocol {

    public static final String REQUEST_DEPOSIT = "kr.ssok.kafka.messaging.request.deposit";
    public static final String REQUEST_WITHDRAW = "kr.ssok.kafka.messaging.request.withdraw";
    public static final String SEND_TEST_MESSAGE = "kr.ssok.kafka.messaging.test.message";

    public static final String VALIDATE_ACCOUNT = "kr.ssok.kafka.messaging.request.validate.account";
    public static final String CHECK_DORMANT = "kr.ssok.kafka.messaging.request.check.dormant";
    public static final String CHECK_BALANCE = "kr.ssok.kafka.messaging.request.check.balance";
    public static final String COMPENSATE_DEPOSIT = "kr.ssok.kafka.messaging.request.compensate.deposit";
    public static final String REQUEST_COMPENSATE = "kr.ssok.kafka.messaging.request.compensate";
}
