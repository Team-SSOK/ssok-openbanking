//package kr.ssok.ssokopenbanking.global.comm.promise;
//
//import lombok.RequiredArgsConstructor;
//import org.apache.kafka.clients.consumer.ConsumerRecord;
//import org.springframework.kafka.requestreply.RequestReplyFuture;
//
///**
// * 프로미스
// * 프로미스 요청을 보내면 CommQueryPromise를 받습니다.
// */
//@RequiredArgsConstructor
//public class CommQueryPromise {
//    private final RequestReplyFuture<String, Object, Object> future;
//
//    /**
//     * 요청에대한 응답을 받기전까지 해당 스레드를 대기합니다.
//     * timeout 초과시 예외가 발생합니다.
//     *
//     * @return
//     * @throws Exception
//     */
//    public PromiseMessage get() throws Exception {
//        ConsumerRecord<String, Object> response = future.get();
//        return new PromiseMessage(response);
//    }
//
//}
