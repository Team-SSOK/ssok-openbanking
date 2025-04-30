package kr.ssok.ssokopenbanking.authentication.dto.request;

import lombok.Getter;

@Getter
public class ApiKeyIssueRequestDto {
    private String serviceName;
    private String adminName;
    private String domain;
}
