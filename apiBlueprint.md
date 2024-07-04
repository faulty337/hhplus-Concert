### 🪙 유저 대기열 토큰

1. 설명
    1. **대기열**이 핵심이며 대기열을 위해서 토큰을 제공하고 토큰을 받거나 받지 못한 경우에 따라 응답을 다르게 해야 한다.
    2. 프론트에서 polling 방식으로 약 1초마다 보낸다고 가정
2. API
    - Endpoint:  `/concert/waiting`
    - Method: `GET`
    
<details>
  <summary>Request</summary>
  
  ```bash
  curl -X GET 'http://localhost:8080/concert/waiting' \
  -H 'Authorization: {token}'
  ```
</details>

<details>
  <summary>Response</summary>
  
  ```json
  {
      "waitingNumber": 999,
      "token": "{token}",
      "isProcessing": "false"
  }
  ```
- 잘못된 토큰, 만료, 없는 토큰 상황 시 새로운 토큰을 발급
- 토큰이 존재한다면 그에 맞는 대기열 번호와 토큰을 그대로 전달
</details>


  

---

### 📆 예약 가능 날짜 조회

1. 설명
    1. 예약이 가능한 날짜에 대한 조회 기능이다.
    2. Concert의 세션에 대한 정보를 반환해야 한다.
        1. 세션은 하나의 콘서트가 여러 날짜, 시간에 따라 존재할 수 있으며 이 세션에 대한 정보를 줘야 한다.
2. API
    - Endpoint:  `/concert/session/{concertId}`
    - Method: `GET`

<details>
  <summary>Request</summary>
  
  ```bash
  curl -X GET 'http://localhost:8080/concert/session/{concertId}'
  ```
</details>

<details>
  <summary>Response</summary>
  
  ```json
  [
      {
          "sessionId": 1,
          "date": "2024-07-03",
          "available": 23
      },
      {
          "sessionId": 2,
          "date": "2024-07-04",
          "available": 23
      },
      {
          "sessionId": 3,
          "date": "2024-07-05",
          "available": 23
      },
      {
          "sessionId": 6,
          "date": "2024-07-06",
          "available": 23
      }
  ]
  ```
</details>

<details>
  <summary>Exception Response</summary>
  
  ```json
  {
      "errorCode": 404,
      "msg": "존재하지 않는 콘서트입니다."
  }
  ```
</details>

---

### 💺 예약 가능 좌석 조회

1. 설명
    1. 예약이 가능한 좌석에 대한 조회 기능이다.
    2. 세션에 대한 전체 좌석(예약 포함) 정보를 보여줘야 한다.
2. API
    - Endpoint:  `/concert/seat/{sessionId}`
    - Method: `GET`

<details>
  <summary>Request</summary>
  
  ```bash
  curl -X GET 'http://localhost:8080/concert/seat/{sessionId}'
  ```
</details>

<details>
  <summary>Response</summary>
  
  ```json
  {
      "date": "2024-07-04",
      "seatList": [
          {
              "seatNumber": 1,
              "isReservation": "false"
          },
          {
              "seatNumber": 2,
              "isReservation": "true"
          },
          {
              "seatNumber": 3,
              "isReservation": "true"
          },
          {
              "seatNumber": 50,
              "isReservation": "false"
          }
      ]
  }
  ```
</details>

<details>
  <summary>Exception Response</summary>
  
  ```json
  {
      "errorCode": 404,
      "msg": "존재하지 않는 날짜입니다."
  }
  ```
</details>

---

### 💰 충전

1. 설명
    1. 유저가 가지고 있는 포인트의 충전 기능
    2. 토큰의 유효성 검사 필요
2. API
    - Endpoint:  `/concert/charge`
    - Method: `PATCH`

<details>
  <summary>Request</summary>
  
  ```bash
  curl -X PATCH 'http://localhost:8080/concert/charge' \
  -H 'Authorization: {token}'
  ```
</details>

<details>
  <summary>Response</summary>
  
  ```json
  {
      "userId": "HappyCat",
      "balance": 300
  }
  ```
</details>

<details>
  <summary>Exception Response</summary>
  
  ```json
  {
      "errorCode": 403,
      "msg": "유효하지 않은 토큰입니다."
  }
  ```
</details>

---

### ✅ 예약

1. 설명
    1. 좌석에 대해 임시 예약을 거는 기능
    2. 토큰의 유효성 검증이 필요하며 이에 따른 Exception 처리 필요
2. API
    - Endpoint:  `/concert/reservation`
    - Method: `POST`

<details>
  <summary>Request</summary>
  
  ```bash
  curl -X POST 'http://localhost:8080/concert/reservation' \
  -H 'Authorization: {token}' \
  -d '{
      "sessionId": 1,
      "seatNumber": 4
  }'
  ```
</details>

<details>
  <summary>Response</summary>
  
  ```json
  {
      "reservationId": 23
  }
  ```
</details>

<details>
  <summary>Exception Response</summary>
  
  ```json
  {
      "errorCode": 404,
      "msg": "존재하지 않는 세션입니다."
  },
  {
      "errorCode": 409,
      "msg": "이미 예약된 좌석입니다."
  },
  {
      "errorCode": 401,
      "msg": "토큰이 만료되었습니다."
  },
  {
      "errorCode": 403,
      "msg": "아직 대기열 처리 중입니다."
  }
  ```
</details>

---

### 💸 결제 기능

1. 설명
    1. 임시 예약된 좌석에 대한 결제 기능
    2. 토큰의 유효성 검증이 필요하며 이에 따른 Exception 처리 필요
2. API
    - Endpoint:  `/concert/payment`
    - Method: `POST`

<details>
  <summary>Request</summary>
  
  ```bash
  curl -X POST 'http://localhost:8080/concert/payment' \
  -H 'Authorization: {token}' \
  -d '{
      "reservationId": 23
  }'
  ```
</details>

<details>
  <summary>Response</summary>
  
  ```json
  {
      "sessionId": 1,
      "date": "2024-07-04",
      "seatNumber": 33
  }
  ```
</details>

<details>
  <summary>Exception Response</summary>
  
  ```json
  {
      "errorCode": 404,
      "msg": "존재하지 않는 예약입니다."
  },
  {
      "errorCode": 409,
      "msg": "이미 결제된 예약입니다."
  },
  {
      "errorCode": 401,
      "msg": "토큰이 만료되었습니다."
  },
  {
      "errorCode": 403,
      "msg": "아직 대기열 처리 중입니다."
  }
  ```
</details>
