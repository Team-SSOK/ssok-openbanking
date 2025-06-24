# SSOK OPENBANKING

LG CNS Am Inspire Camp 1기 3조 금융팀의 최종 프로젝트 **SSOK OPENBANKING** 백엔드(Spring Boot) 애플리케이션입니다.  
이 프로젝트는 SSOK APP의 오픈뱅킹 기능을 제공하며, 채널계인 SSOK 앱과 계정계인 SSOK BANK 사이의 통신을 지원합니다.

---

## 🏛️ 주요 기능

- **송금**: 송금 중계
- **계좌 조회**: 은행별 계좌 조회 요청
- **잔액 조회**: 계좌의 현재 잔액 조회 요청
- **계좌 실명 조회**: 계좌의 예금주성명 확인 요청
- **API 키 발급**: 오픈뱅킹의 API를 사용할 수 있게 해 주는 키를 발급
---

## 🧱 기술 스택

| 구성 요소           | 설명                       |
|-----------------|--------------------------|
| Spring Boot     | RESTful API 서버 구현        |
| Spring Data JPA | 데이터 접근 계층 구성             |
| MariaDB         | 개발용 임베디드 DB 또는 운영용 RDBMS |
| Lombok          | 반복 코드 자동 생성              |

---

## ⚙️ 설치 및 실행 (개발 환경 기준)

1. **소스 코드 클론**
    ```bash
    git clone https://github.com/Team-SSOK/ssok-openbanking.git
    cd ssok-openbanking
    ```
2. **환경 설정**  
   `src/main/resources/application.yml` 또는 `application-dev.yml`에 DB 연결 및 포트, 기타 설정 구성.
3. **의존성 설치 및 컴파일**

4. **서버 실행**

5. **API 테스트**
    - 자세한 API 명세는 [openbanking_spec.md](./openbanking_spec.md)를 참조하세요.
    - [POST] `/api/openbank/transfers` – 송금 요청
    - [POST] `/api/openbank/accounts/request` – 은행별 계좌 조회 요청
    - [POST] `/api/openbank/account/balance` – 계좌 잔액 조회
    - [POST] `/api/openbank/account/verify-name` – 계좌 실명 조회
    - [POST] `api/openbank/openapikey` – API 키 발급

---

## 🔧 개발 관련 설정

- **프로필 활성화**  
  `-Dspring.profiles.active=dev` 또는 `dev` 프로필 기본
- **DB 초기화**: `schema.sql` 로딩
- **테스트 코드**: JUnit 기반 단위/통합 테스트

---

## 🧪 테스트 및 검증

- IDE 내 테스트 실행
- 통합 테스트: MockMvc 활용한 REST API 검증

---

## 🚀 배포

- Dockerfile 및 CI/CD 설정 필요 (DevOps/ssok-deploy 리포지토리 참고)
- 운영 DB(MySQL 등) 및 AWS/GCP 배포 환경 연동

---

## 👨‍👩‍👧‍👦 팀 정보

- **Team‑SSOK** – LG CNS Am Inspire Camp 1기 3조 금융팀
- **관련 레포지토리**
    - SSOK APP 프론트엔드: `ssok-frontend`
    - 배포 자동화: `ssok-deploy`
    - 뱅크 연동: `ssok-bank`

---



