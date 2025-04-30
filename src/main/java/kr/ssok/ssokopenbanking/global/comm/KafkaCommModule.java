package kr.ssok.ssokopenbanking.global.comm;


import kr.ssok.ssokopenbanking.global.comm.promise.CommQueryPromise;
import org.springframework.kafka.requestreply.ReplyingKafkaTemplate;
import org.springframework.kafka.support.SendResult;

import java.util.function.BiConsumer;

/**
 * 카프카 통신 모듈
 * 프로미스, 단방향 메세지 전송을 지원합니다.
 */
public interface KafkaCommModule {
    /**
     * 프로미스 쿼리를 서버에 요청합니다.
     * 요청을 보내면 그에대한 응답 메세지를 받습니다. (타임아웃: 30초)
     *
     * @param key     식별자 키
     * @param request DTO 객체
     * @return CommQueryPromise
     */
    public CommQueryPromise sendPromiseQuery(String key, Object request);

    /**
     * 프로미스 쿼리를 서버에 요청합니다.
     * 요청을 보내면 그에대한 응답 메세지를 받습니다.
     *
     * @param key     식별자 키
     * @param request DTO 객체
     * @param timeout 타임아웃 (초)
     * @return CommQueryPromise
     */
    public CommQueryPromise sendPromiseQuery(String key, Object request, int timeout);

    /**
     * 단방향 메세지를 전송합니다.
     *
     * @param key     식별자 키
     * @param request DTO 객체
     * @return Message
     */
    public Message sendMessage(String key, Object request);

    /**
     * 단방향 메세지를 전송합니다.
     * 전송결과를 비동기 콜백함수를 통해 확인할 수 있습니다.
     *
     * @param key      식별자 키
     * @param request  DTO 객체
     * @param callback 콜백 함수
     * @return Message
     */
    public Message sendMessage(String key, Object request, BiConsumer<? super SendResult<String, Object>, ? super Throwable> callback);

    /**
     * ReplyingKafkaTemplate을 반환합니다.
     *
     * @return ReplyingKafkaTemplate
     */
    public ReplyingKafkaTemplate<String, Object, Object> getReplyingKafkaTemplate();
}
