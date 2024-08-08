# 서비스 분리

현재 작성된 프로젝트의 구조는

`Concert`

`ConcertSession`

`ConcertSeat`

`Reservation`

`Payment`

로 되어 있다.

### 서비스 분리

이를 만약 서비스로 구분해야 한다면

1. Concert와 ConcertSession, ConcertSeat는 같은 Concert라는 주체를 다루기에 같은 서비스로 포함된다.
2. Reservation은 Concert에서만 이루어지는 것이 아닌 ‘확장’시 다른 곳에서 예약이 포함될 수 있다.
3. Payment도 마찬가지로 Concert에 대한 결제를 다루는 것이 아닌 ‘확장’시 다른 곳에서도 사용될 수 있는 ‘결제’이기에 따로 구성된다.
4. Waiting은 ‘트래픽 제어’라는 목적이기에 따로 구성된다.

```java
Waiting
Concert
├── Concert
├── ConcertSession
└── ConcertSeat
Reservation
Payment
```

### 트랜잭션 범위 파악

각 모든 기능에 대해 트랜잭션 범위에 대해 파악하면

1. **getWaitingInfo()** 
    - 유저 대기 상태를 가져오는 기능이다.
    
    ```java
    getWaitingInfo(){
    	UserServicer.getUser()
    	JwtService.isProcessingToken()
    	WaitingService.enqueueToProcessingQueueIfAvailable()
    	WaitingService.getWaitingNumber()
    }
    
    ```
    
    - 위와 같이 구성되어 있으며 여기서 봐야 할 부분은 User에 대한 정보를 가져오고 이후 Token 정보까지 가져오게 된다.
    - 이후 Waiting 서비스를 이용해 유저를 처리열 혹은 대기열로 넣게 된다.
    - 해당 부분은 요구 사항에서 polling으로 실행되기에 따로 동시성을 제어하지 않았으나 Redis를 이용하는 시점에서 해당 부분은 어느 정도 처리가 되었다.
    - 여기서 User와 Jwt부분은 인증 부분으로 대기열 정보를 반환하는 되고 나오는 부분이기에 만약 확장된다면 ‘인증’을 담당하는 부분이기에 따로 분리가 된다.
    - 해당 기능은 polling이라는 전제하에 호출이 되며 인증서쪽에서 User의 정보를 가져오고 Redis를 확인하기에 따로 DB 접근이 없다.
        - 즉 Transaction으로 관리 될 필요 없다.
    
    ```java
    getWaitingInfo(){
    	WaitingService.enqueueToProcessingQueueIfAvailable()
    	WaitingService.getWaitingNumber()
    }
    
    Authenticate(){
    	UserServicer.getUser()
    	JwtService.isProcessingToken()
    }
    ```
    

1. **getSessionDate(), getSessionSeat()**
    - Concert에 해당하는 Session 즉 오픈 날짜와 좌석을 조회하는 기능.
    - 두 조회 기능은 Concert 서비스 내에서 처리가 가능하며 단순 조회 기능이다.

1. **charge(), getBallance()**
    - 유저의 잔액 조회, 충전에 대한 기능이다.
    - 마찬가지로 Payment내에서 처리가 가능한 기능이다.

1. **reserveConcert()**
    - Concert에 대해 결제 전 예약을 하는 기능이다.
    
    ```java
    @Transactional
    reserveConcert(){
    	UserService.getUser()
    	ConcertService.getAvailableReservationSeats()
    	ConcertService.ReserveConcertSeat()
    	ReservationService.createReservation()
    }
    ```
    
    - 이전과 같이 UserService는 따로 인증으로 분리되어야 한다.
    - 때문에 Concert와 Reservation이 남게 되는데 이는 동시성 이슈가 있는 부분으로 낙관적 DB락이 걸려 있다.
        - Version은 Transaction과 별개로 실제 저장된 데이터에 대해 버전이 붙기에 크게 신경쓰지 않아도 되었다.
    - 만약 서비스를 분리한다면 Transaction에 대해 관리가 되어야 한다.
        - ‘예약’이라는 작업단위를 Transaction이 감싸야 하기 때문에 만약 이를 분리한다면 각 각 트랜잭션간 실패시 보상 트랜잭션을 생각해봐야 한다.
        - `좌석에 대한 예약 상태 변환 → 예약 생성 실패` 에서 좌석에 예약 상태를 원상복구 시키는 로직이 필요하게 된다.
2. **Payment**
    - 예약에 대해 결제하는 기능이다.
    
    ```java
    @Transactional
    payment(){
    	UserService.getUser()
    	ReservationService.getReservation()
    	UserService.usePoint()
    	ReservationService.confirmReservationStatus()
    	PaymentService.addPaymentHistory()
    	WaititngService.moveUserToProcessingQueue()
    }
    ```
    
    - UserService 부분은 인증으로 간다.
    - `getReservation` 해당 예약 정보를 가져와야 하며,
    - `confirmReservationStatus` 예약 상태를 결제 완료로 바꿔야 한다.
    - `moveUserToProcessingQueue`마지막으로 결제 완료시 해당 유저의 대기열을 만료 시킨다.
        - 해당 부분은 결제 로직 이외에 부가 로직이기 냅다 Event를 던져 놓고 결제 로직 만 처리하면 된다.
    - 마찬가지로 희박하지만 동시성 이슈가 있을 수 있는 부분으로 낙관적 락을 걸어놨었다.
        - 예약과 마찬가지로 낙관적 락의 버전은 Transaction과 별개이기에 동시성 이슈에 관련한 Transaction 이슈는 배재했다.
    - 위 일련의 과정들이 하나의 Transaction으로 실행되어야 하는데 서비스 분리 시 각각의 작업들 실패시 RollBack 즉, 보상 트랜잭션을 생각해봐야 한다.
        - 결제에서 타 서비스가 필요한 부분은 위에 3개가 된다.
            - getReservation에서는 단수 조회 및 이전 로직에서도 단순 조회만 있기에 보상이 필요 없게 된다.
            - confirmReservationStatus에서 예약의 상태 변환 부분은 유저가 돈을 사용하고 난 다음에 처리된다. 즉, 해당 로직 실패 시 Point를 복구 해야 한다.
                - 이외에 결제 에서도 마찬가지로 이후 로직에서 실패 시 해당 예약의 상태를 복구해줄 필요가 있다.