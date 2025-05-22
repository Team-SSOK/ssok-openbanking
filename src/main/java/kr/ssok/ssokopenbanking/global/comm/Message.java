//package kr.ssok.ssokopenbanking.global.comm;
//
//import lombok.RequiredArgsConstructor;
//import org.springframework.kafka.support.SendResult;
//
//import java.util.concurrent.CompletableFuture;
//import java.util.function.BiConsumer;
//
///**
// * 단방향 메시지
// * 메시지 전송에 대한 전송 결과를 확인할 수 있습니다. (콜백 등록 필요)
// */
//@RequiredArgsConstructor
//public class Message {
//    private final CompletableFuture<SendResult<String, Object>> future;
//
//    Message(CompletableFuture<SendResult<String, Object>> future,
//            BiConsumer<? super SendResult<String, Object>, ? super Throwable> callback)
//    {
//        this.future = future;
//        this.addCallback(callback);
//    }
//
//    private void addCallback(BiConsumer<? super SendResult<String, Object>, ? super Throwable> callback) {
//        if (callback != null) this.future.whenComplete(callback);
//    }
//
//}