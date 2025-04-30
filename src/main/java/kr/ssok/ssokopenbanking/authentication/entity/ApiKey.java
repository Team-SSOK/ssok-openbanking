package kr.ssok.ssokopenbanking.authentication.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.TimeToLive;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@RedisHash("api-key")
public class ApiKey {
    @Id
    private String key;
    private String serviceName;
    private String adminName;
    private String domain;

    @TimeToLive
    private Long ttl; // 초 단위
}
