# OPENBANKING API

## 1. 송금 요청

- **URL**: `POST /api/openbank/transfers`

- **Request Body**
  
  | **항목**            | **타입** | **필수 여부** | **default** | **설명**      | **비고** |
  |-------------------|--------|-----------|-------------|-------------|--------|
  | sendAccountNumber | String | Y         | -           | 출금계좌번호      | 식별자    |
  | sendBankCode      | int    | Y         | -           | 출금은행코드      |        |
  | sendName          | String | Y         | -           | 출금자 이름 (본인) |        |
  | recvAccountNumber | String | Y         | -           | 입금계좌번호      |        |
  | recvBankCode      | int    | Y         | -           | 입금은행코드      |        |
  | recvName          | String | Y         | -           | 입금 상대방 이름   |        |
  | amount            | Long   | Y         | -           | 금액          |        |

- **Response (200)**
  ```json
  {
      "isSuccess": true,
      "code": "200",
      "message": "송금에 성공했습니다.",
      "result": {
          "transactionId": "70fbb7a3-22dc-490a-81a3-e25206c095fd",
          "status": "COMPLETED",
          "message": "송금이 성공적으로 처리되었습니다."
      }
  }
  ```

---

## 2. 은행별 계좌 조회 요청

- **URL**: `POST /api/openbank/accounts/request`
- **Request Body**
    
    | **항목**      | **타입** | **필수 여부** | **default** | **설명** | **비고** |
    |-------------|--------|-----------|-------------|--------|--------|
    | username    | String | Y         | -           | 이름     | 식별자    |
    | phoneNumber | String | Y         | -           | 전화번호   | 식별자    |

- **Response (200)**
  ```json
  {
      "isSuccess": true,
      "code": 200,
      "message": "전체 계좌 조회가 완료되었습니다.",
      "result": {
          "accounts": [
              {
                  "bankCode": 1,
                  "accountNumber": "11111111-111111"
                  "accountTypeCode": 1
              },
              ...
          ]
      }
  }
  ```
---

## 3. 계좌 잔액 조회

- **URL**: `POST  /api/openbank/account/balance`
- **Request Body**
    
    | **항목**        | **타입** | **필수 여부** | **default** | **설명** | **비고** |
    |---------------|--------|-----------|-------------|--------|--------|
    | accountNumber | String | Y         | -           | 계좌번호   | 식별자    |
    | bankCode      | String | Y         | -           | 비밀번호   | 식별자    |

- **Response (200)**

  ```json
   {
	  "isSuccess": true,
	  "code" : 200,
      "message": "계좌 잔액 조회가 완료되었습니다.",
      "result": {
	      "balance": 1000000
      }
   }
  ```

---

## 4. 계좌 실명 조회

- **URL**: `POST  /api/openbank/account/verify-name`
- **Request Body**
    
    | **항목**        | **타입** | **필수 여부** | **default** | **설명** | **비고** |
    |---------------|--------|-----------|-------------|--------|--------|
    | accountNumber | String | Y         | -           | 계좌번호   | 식별자    |
    | bankCode      | String | Y         | -           | 은행코드   | 식별자    |

- **Response (200)**

  ```json
  {
	  "isSuccess": true,
	  "code" : 200,
      "message": "계좌 실명 조회가 완료되었습니다.",
      "result": {
	      "username": "최지훈"
      }
  }
  ```

---

## 5. API 키 발급

- **URL**: `POST  /api/openbank/openapikey`
- **Request Body**
    
    | **항목**      | **타입** | **필수 여부** | **default** | **설명**   | **비고** |
    |-------------|--------|-----------|-------------|----------|--------|
    | serviceName | String | Y         | -           | 서비스명     | 식별자    |
    | adminName   | String | Y         | -           | 서비스 관리자명 | 식별자    |
    | domain      | String | Y         | -           | 서비스 도메인  | 식별자    |

- **Response (200)**

  ```json
  {
    "isSuccess": true,
    "code": "200",
    "message": "요청이 성공적으로 처리되었습니다.",
    "result": {
        "apiKey": "b3ff2044-58f1-40c0-9e41-95ec7665a92d"
    }
  }
  ```


---