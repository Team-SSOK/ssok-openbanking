package kr.ssok.ssokopenbanking.authentication.service;

import kr.ssok.ssokopenbanking.authentication.dto.response.ApiKeyIssueResultDto;

public interface ApiKeyService {
    ApiKeyIssueResultDto generateApiKey(String serviceName, String adminName, String domain);
}
