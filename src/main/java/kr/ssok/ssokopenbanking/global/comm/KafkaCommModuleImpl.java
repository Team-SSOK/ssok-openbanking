package kr.ssok.ssokopenbanking.global.comm;

import jakarta.annotation.PostConstruct;
import kr.ssok.ssokopenbanking.global.comm.promise.CommQueryPromise;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.requestreply.ReplyingKafkaTemplate;
import org.springframework.kafka.requestreply.RequestReplyFuture;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.concurrent.CompletableFuture;
import java.util.function.BiConsumer;

@Slf4j
@Component
@RequiredArgsConstructor
public class KafkaCommModuleImpl implements KafkaCommModule {
    private final ReplyingKafkaTemplate<String, Object, Object> replyingKafkaTemplate;

    private final KafkaTemplate<String, Object> kafkaTemplate;

    @Value("${spring.kafka.request-topic}")
    private String requestTopic;

    @Value("${spring.kafka.push-topic}")
    private String pushTopic;

    @PostConstruct
    public void init() {
        replyingKafkaTemplate.start();
    }

    @Override
    public CommQueryPromise sendPromiseQuery(String key, Object request) {
        return this.sendPromiseQuery(key, request, 30);
    }

    @Override
    public CommQueryPromise sendPromiseQuery(String key, Object request, int timeout) {
        ProducerRecord<String, Object> record =
                new ProducerRecord<>(requestTopic, key, request);

        log.info("Sending Promise Request: {}", request);

        RequestReplyFuture<String, Object, Object> future =
                this.replyingKafkaTemplate.sendAndReceive(record, Duration.ofSeconds(timeout));

        return new CommQueryPromise(future);
    }

    @Override
    public Message sendMessage(String key, Object request) {
        return this.sendMessage(key, request, null);
    }

    @Override
    public Message sendMessage(String key, Object request, BiConsumer<? super SendResult<String, Object>, ? super Throwable> callback) {
        ProducerRecord<String, Object> record =
                new ProducerRecord<>(pushTopic, key, request);

        CompletableFuture<SendResult<String, Object>> future = kafkaTemplate.send(pushTopic, key, request);

        return new Message(future, callback);
    }

    @Override
    public ReplyingKafkaTemplate<String, Object, Object> getReplyingKafkaTemplate() {
        return replyingKafkaTemplate;
    }

}
