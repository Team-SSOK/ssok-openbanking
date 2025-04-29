package kr.ssok.ssokopenbanking.authentication.service;

import kr.ssok.ssokopenbanking.authentication.dto.response.ApiKeyIssueResultDto;
import kr.ssok.ssokopenbanking.authentication.entity.ApiKey;
import kr.ssok.ssokopenbanking.authentication.repository.ApiKeyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ApiKeyServiceImpl implements ApiKeyService {

    private final ApiKeyRepository apiKeyRepository;

    // TTL은 180일로 설정
    private static final long DEFAULT_TTL_SECONDS = 180L * 24 * 60 * 60;

    /**
     * 서비스에 대한 API 키 생성
     * @param serviceName (서비스명)
     * @param adminName (서비스 관리자명)
     * @param domain (서비스 도메인)
     * @return 생성된 api 키
     */
    @Override
    public ApiKeyIssueResultDto generateApiKey(String serviceName, String adminName, String domain) {

        String apiKey = UUID.randomUUID().toString();

        ApiKey newApiKey = ApiKey.builder()
                .key(apiKey)
                .serviceName(serviceName)
                .adminName(adminName)
                .domain(domain)
                .ttl(DEFAULT_TTL_SECONDS)
                .build();

        apiKeyRepository.save(newApiKey);

        return ApiKeyIssueResultDto.builder()
                .apiKey(apiKey)
                .build();
    }
}
