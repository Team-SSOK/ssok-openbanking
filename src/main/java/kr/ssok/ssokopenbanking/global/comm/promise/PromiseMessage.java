//package kr.ssok.ssokopenbanking.global.comm.promise;
//
//import kr.ssok.ssokopenbanking.global.comm.JsonUtil;
//import lombok.RequiredArgsConstructor;
//import org.apache.kafka.clients.consumer.ConsumerRecord;
//
///**
// * 프로미스 메시지
// * CommQueryPromise에서 PromiseMessage을 가져올 수 있습니다.
// */
//@RequiredArgsConstructor
//public class PromiseMessage {
//
//    private final ConsumerRecord<String, Object> response;
//
//    public <T> T getDataObject(Class<T> responseType) {
//        Object value = response.value();
//
//        if (value == null || !(value instanceof String))
//            throw new RuntimeException("[PromiseMessage] JSON 파싱 실패");
//
//        return JsonUtil.fromJson((String) value, responseType);
//    }
//
//}