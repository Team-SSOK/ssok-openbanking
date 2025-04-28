package kr.ssok.ssokopenbanking.global.enums;

import lombok.Getter;

import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Getter
public enum BankCode {
    SSOK_BANK("01", "SSOK 뱅크"),
    KAKAO_BANK("02", "카카오뱅크"),
    KOOKMIN_BANK("03", "국민은행"),
    SHINHAN_BANK("04", "신한은행"),
    WOORI_BANK("05", "우리은행"),
    HANA_BANK("06", "하나은행"),
    NH_BANK("07", "농협은행"),
    IBK_BANK("08", "기업은행"),
    K_BANK("09", "케이뱅크"),
    TOSS_BANK("10", "토스뱅크");

    private final String code;
    private final String description;

    BankCode(String code, String description) {
        this.code = code;
        this.description = description;
    }

    private static final Map<String, BankCode> BY_CODE =
            Stream.of(values()).collect(Collectors.toMap(BankCode::getCode, e -> e));

    public static BankCode fromCode(String code) {
        BankCode result = BY_CODE.get(code);
        if (result == null) {
            throw new IllegalArgumentException("지원하지 않는 은행코드입니다: " + code);
        }
        return result;
    }
}
