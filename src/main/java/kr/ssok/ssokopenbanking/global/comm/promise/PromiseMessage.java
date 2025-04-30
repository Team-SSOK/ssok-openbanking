package kr.ssok.ssokopenbanking.global.comm.promise;

import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.consumer.ConsumerRecord;

/**
 * 프로미스 메시지
 * CommQueryPromise에서 PromiseMessage을 가져올 수 있습니다.
 */
@RequiredArgsConstructor
public class PromiseMessage {

    private final ConsumerRecord<String, Object> response;

    public <R> R getDataObject(Class<R> responseType) {
        Object value = response.value();

        if (value == null) return null;

        try
        {
            return responseType.cast(value);
        }
        catch (ClassCastException e)
        {
            throw new ClassCastException(String.format(
                    "응답 값을 %s 타입으로 변환할 수 없습니다. 실제 타입: %s",
                    responseType.getName(),
                    value.getClass().getName()
            ));
        }
    }

}