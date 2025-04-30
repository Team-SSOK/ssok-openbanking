package kr.ssok.ssokopenbanking.authentication.controller;

import kr.ssok.ssokopenbanking.authentication.dto.request.ApiKeyIssueRequestDto;
import kr.ssok.ssokopenbanking.authentication.dto.response.ApiKeyIssueResultDto;
import kr.ssok.ssokopenbanking.authentication.service.ApiKeyService;
import kr.ssok.ssokopenbanking.global.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/openbank")
@RequiredArgsConstructor
public class ApiKeyController {

    private final ApiKeyService apiKeyService;

    /**
     * API 키 발급 API
     */
    @PostMapping("/openapikey")
    public ResponseEntity<ApiResponse<ApiKeyIssueResultDto>> issueApiKey(@RequestBody ApiKeyIssueRequestDto requestDto) {

        System.out.println("1");

        ApiKeyIssueResultDto apiKey = apiKeyService.generateApiKey(requestDto.getServiceName(), requestDto.getAdminName(), requestDto.getDomain());

        return ApiResponse.success(apiKey).toResponseEntity();
    }

}
