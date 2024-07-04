### 🪙유저 대기열 토큰

1. 설명
    1. **대기열**이 핵심이며 대기열을 위해서 토큰을 제공하고 토큰받거나 받지 못하거나 그에 따른 응답을 다르게 해야한다.
    2. 프론트에서 polling 방식으로 약 1초마다 보낸다고 가정
2. API
    - Endpoint:  `/concert/wating`
    - Method: `GET`
    - Request
        
        ```bash
        curl -x GET 'http://localhost:8080/concert/wating' \
        -H 'Authorization: {token}'
        ```
        
    - Response
        
        ```json
        {
        	"watingNumber":999,
        	"token":{token},
        	"isProcessing":"false"
        }
        ```
        
        - 잘못된 토큰, 만료, 없는 토큰 상황 시 새로운 토큰을 발급
        - 토큰이 존재한다면 그에 맞는 대기열 번호와 토큰을 그대로 전달

### 📆예약 가능 날짜 조회

1. 설명
    1. 예약이 가능한 날짜에 대한 조회 기능이다. 
    2. 때문에 Concert의 세션에 대한 정보를 반환해야 한다.
        1. 세션은 하나의 콘서트가 여러 날짜, 시간에 따라 존재 할 수 있으며 이 세션에 대한 정보를 줘야 한다. 
2. API
    - Endpoint:  `/concert/session/{concertId}`
    - Method: `GET`
    - Request
        
        ```bash
        curl -x GET 'http://localhost:8080/concert/session/{concertId}'
        ```
        
    - Response
        
        ```json
        {
        	[
        		{
        			"sessionId":1,
        			"date":"2024-07-03",
        			"abailable":23
        		},
        		{
        			"sessionId":2,
        			"date":"2024-07-04",
        			"abailable":23
        		},
        		{
        			"sessionId":3,
        			"date":"2024-07-05",
        			"abailable":23
        		}
        		···
        		{
        			"sessionId":6,
        			"date":"2024-07-06",
        			"abailable":23
        		}
        	]
        }
        ```
        
    - Exception Response
        - 콘서트 존재X
            
            ```json
            "errorCode":404,
            "msg":"존재하지 않는 콘서트 입니다."
            ```
            

### 💺예약 가능 좌석 조회

1. 설명
    1. 예약이 가능한 좌석에 대한 조회 기능이다.
    2. 세션에 대한 전체 좌석(예약 포함) 정보를 보여줘야 한다.
2. API
    - Endpoint:  `/concert/seat/{sessionId}`
    - Method: `GET`
    - Request
        
        ```bash
        curl -x GET 'http://localhost:8080/concert/seat/{sessionId}'
        ```
        
    - Response
        
        ```json
        {
        	"date":"2024-07-04"
        	"seatList":
        		[
        			{
        				"seatNumber":1,
        				"isReservation":"false"
        			},
        			{
        				"seatNumber":2,
        				"isReservation":"true"
        			},
        			{
        				"seatNumber":3,
        				"isReservation":"true"
        			}
        			···
        			{
        				"seatNumber":50,
        				"isReservation":"false"
        			}
        		]
        }
        ```
        
    - Exception Response
        - 날짜 존재X
            
            ```json
            "errorCode":404,
            "msg":"존재하지 않는 날짜입니다."
            ```
            
    

### 💰 충전

1. 설명
    1. 유저가 가지고 이는 포인트의 충전 기능
    2. 토큰의 유효성 검사 필요
2. API
    - Endpoint:  `/concert/seat/{sessionId}`
    - Method: `PATCH`
    - Request
        
        ```bash
        curl -x GET 'http://localhost:8080/concert/seat/{sessionId}'
        -H 'Authorization: {token}'
        ```
        
    - Response
        
        ```json
        {
        	"userId":"HappyCat",
        	"balance":300
        }
        ```
        
    - Exception Response
        - 토큰 유효성X
            
            ```json
            "errorCode":403,
            "msg":"유효하지 않은 토큰입니다."
            ```
            

### ✅예약

1. 설명
    1. 좌석에 대해 임시 예약을 거는 기능
    2. 토큰의 유효성 검증이 필요하며 이에 따른 Exception 처리 필요
2. API
    - Endpoint:  `/concert/reservation`
    - Method: `POST`
    - Request
        
        ```bash
        curl -x GET 'http://localhost:8080/concert/reservation'
        -H 'Authorization: {token}'
        ```
        
        - RequestBody
            
            ```json
            {
            	"sessionId":1,
            	"seatNumber":4
            }
            ```
            
    - Response
        
        ```json
        {
        	"reservationId":23,
        }
        ```
        
        - Exception Response
            - 세션 존재X
                
                ```json
                "errorCode":404,
                "msg":"존재하지 않는 세션입니다."
                ```
                
            - 좌석 이미 예약
                
                ```json
                "errorCode":409,
                "msg":"이미 예약된 좌석입니다."
                ```
                
            - 토큰 만료
                
                ```json
                "errorCode":401,
                "msg":"토큰이 만료되었습니다." 
                ```
                
            - 대기열
                
                ```json
                "errorCode":403,
                "msg":아직 대기열 처리 중입니다.
                ```
                

### 💸결제 기능

1. 설명
    1. 임시 예약된 좌석에 대한 결제 기능
    2. 토큰의 유효성 검증이 필요하며 이에 따른 Exception 처리 필요
2. API
    - Endpoint:  `/concert/payment`
    - Method: `POST`
    - Request
        
        ```bash
        curl -x GET 'http://localhost:8080/concert/reservation'
        -H 'Authorization: {token}'
        ```
        
        - RequestBody
            
            ```json
            {
            	"reservationId":23
            }
            ```
            
    - Response
        
        ```json
        {
        	"sessionId":1,
        	"date":"2024-07-04",
        	"seatNumber":33,
        }
        ```
        
        - Exception Response
            - 예약 존재X
                
                ```json
                "errorCode":404,
                "msg":"존재하지 않는 예약입니다."
                ```
                
            - 이미 결제
                
                ```json
                "errorCode":409,
                "msg":"이미 결제된 예약입니다."
                ```
                
            - 토큰 만료
                
                ```json
                "errorCode":401,
                "msg":"토큰이 만료되었습니다." 
                ```
                
            - 대기열
                
                ```json
                "errorCode":403,
                "msg":아직 대기열 처리 중입니다.
                ```