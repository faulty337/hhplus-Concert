## 문제 원인

- **2개 이상**의 요청이 **동시**에 **동일 데이터**에 대해 수정하고자 할 때 생기는 문제
- ex)
    
    > 현재 잔액 : 10000
    1000 충전 2회
    예상 결과 : 12000
    > 
    
    > 동시에 X
    1. 잔액 조회(10000) -> 잔액 수정(11000)-> 잔액 저장(11000) → 2. 잔액 조회(11000) -> 잔액 수정(12000) -> 잔액 저장(12000)
    > 
    
    > 동시에
    1. 잔액 조회(10000) -> 잔액 수정(11000)-> 잔액 저장(11000)
    2. 잔액 조회(10000) -> 잔액 수정(11000) -> 잔액 저장(11000)
    > 
- 읽는 시점이 동시에 이루어지면 변경 전 값을 읽어오기에 문제 발생

## 문제 발생 예상 기능

### 충전

- 기본 로직
    
    ```java
        @Override
        @Transactional
        public User chargePoint(Long userId, int amount) {
            //유효성 검사
            User user = userRepository.findById(userId).orElseThrow(
                    () -> new CustomException(ErrorCode.NOT_FOUND_USER_ID)
            );
            
            //충전 포인트 양수 확인
            if(amount < 1){
                throw new CustomException(ErrorCode.INVALID_AMOUNT);
            }
            
    				//충전
            user.chargeBalance(amount);
            user = userRepository.save(user);
            return user;
        }
    ```
    
- 문제 발생 시나리오
    - **‘따닥’**(연속적으로 클릭 혹은 빠르게 반복)
    - 자신의 돈에 대한 충전이 빠르게 반복되었을 경우 기존에 읽은 user와 user의 point등이 동시에 읽혀 실수로 눌렀건, 실제로 눌렀건 예상 결과와는 다른 결과를 내뱉게 된다.
    
      ![Untitled](https://github.com/user-attachments/assets/b722c37c-0790-4399-a4a3-1b8338716068)

    
- 해결 방법
    - 중복 요청이 둘다 유효한지(희박하지만 가능성 존재) 무효 한지에 대한 판단 기준이 없기에 **비관적 락**을 통해 요청에 대한 처리를 진행
        
        ```java
        User user = userRepository.findByIdWithLock(userId).orElseThrow(
                () -> new CustomException(ErrorCode.NOT_FOUND_USER_ID)
        );
        ```
        
        ```java
            @Lock(LockModeType.PESSIMISTIC_WRITE)
            @Query("SELECT u FROM User u WHERE u.id = :userId")
            Optional<User> findByIdWithLock(Long userId);
        ```
      ![Untitled 1](https://github.com/user-attachments/assets/849272f4-a9dc-4aa8-b3b3-0cf8970e23a4)
    - 재시작 로직 없이 낙관적 락 이용이 가능하지만, 실제 요청인지에 대한 확인 없이 처리에 대한 부분이 애매하다.
        - 유저의 중복 요청에 대한 결제를.. 받아서 돈을 뽑아 먹어야 한다..
    


    

### 예약

- 기본 로직
    
    ```java
        @Transactional
        public ReservationResponseDto reserveConcert(Long concertId, Long sessionId, Long seatId, Long userId) {
            User user = userService.getUser(userId);
            //유효성 검사
            ConcertSeat concertSeat = concertService.getSeat(concertId, sessionId, seatId);
    
            concertSeat = concertService.getAvailableReservationSeats(concertId, sessionId, seatId);
    
            //통계 · 정보성 logging
            logger.info("Reservation try made: User ID: {}, Concert ID: {}, ConcertSession ID: {}, ConcertSeat ID: {}, Price: {}",
                    user.getId(), concertId, sessionId, seatId, concertSeat.getPrice());
    
            //예약 저장
            Reservation reservation = reservationService.createReservation(new Reservation(user.getId(), sessionId, concertSeat.getId(), concertSeat.getPrice()));
    
            return new ReservationResponseDto(reservation.getId(), reservation.getReservationPrice());
        }
    ```
    
- 문제 발생 시나리오
    - 한 자리에 여러 유저가 동시에 예약 시도
    - 마찬가지로 Seat가 예약 가능한 상태로 동시 요청에서 읽게 되고 동시에 예약 저장 시 한 좌석에 여러 유저가 예약이 가능
    
      ![Untitled 2](https://github.com/user-attachments/assets/0fae3590-247d-4e36-8a44-021ae08b5777)

    
- 해결 방법
    - 자원이 하나이고 이에 대한 예약이 이루어 졌을 때 나머지는 예약은 서비스 상 불가능 하기 때문에 단 하나의 스레드만 요청에 성공하면 된다. 즉, **낙관적 락**에서 재시작 로직없이 처리가 가능하다.
        
        ```java
          try{
              return concertFacade.reserveConcert(
                      requestDto.getConcertId(),
                      requestDto.getSessionId(),
                      requestDto.getSeatId(),
                      requestDto.getUserId()
              );
          }catch (ObjectOptimisticLockingFailureException e){
              throw new CustomException(ErrorCode.NOT_AVAILABLE_SEAT);
          }
        ```
        
        ```java
        public class ConcertSeat {
        	@Version
        	private int version;
        ```
      ![Untitled 3](https://github.com/user-attachments/assets/e0726ec5-6881-4691-96d0-9726b6699b5c)
    - 비관적 락으로 진행 시 하나씩 처리되기 때문에 충돌 상황이 많을 경우에 데이터에 대한 무결성은 보장되지만 현재 하나의 예약이라는 자원의 조건 때문에 첫 스레드 성공 이후 실패가 확정된 다른 스레드에 대한 락과 처리 시간으로 너무 오래 잡히게 되어 효율적이지 않아 보인다.
    - 분산락으로 동시성 제어 진행 시 커넥션을 물고 있는 소모 자원은 비슷하나, 구현 복잡도에 있어서 난이도가 있다는 점에서 낙관적 락이 더 나아보인다.


    

### 결제

- 기본 로직
    
    ```java
        @Transactional
        public PaymentResponseDto payment(long userId, long reservationId){
            //user 유효성 검사
            User user = userService.getUser(userId);
    
            //예약 정보 확인
            Reservation reservation = reservationService.getReservationByUserId(user.getId(), reservationId);
            Concert concert = concertService.getConcertBySessionId(reservation.getConcertSessionId());
            ConcertSeat concertSeat = concertService.getSeat(concert.getId(), reservation.getConcertSessionId(), reservation.getConcertSeatId());
    
            //잔액 차감
            userService.usePoint(user.getId(), concertSeat.getPrice());
    
            //결제 정보 저장
            PaymentHistory paymentHistory = paymentService.addPaymentHistory(new PaymentHistory(concertSeat.getPrice(), user, reservation));
    
            //예약 상태 변환
            reservation.setStatus(Reservation.ReservationStatus.CONFIRMED);
            ConcertSession concertSession = concertService.getSession(reservation.getConcertSessionId());
    
            logger.info("Payment : User ID: {}, Amount : {},", userId, reservation.getReservationPrice());
    
            //처리열 삭제 및 대기열 업데이트
            queueService.removeProcessingByUserId(userId);
    
            return new PaymentResponseDto(concertSession.getId(), concertSession.getSessionTime(), concertSeat.getSeatNumber(), paymentHistory.getAmount());
        }
    ```
    
- 문제 발생 시나리오
    - 이전과 같은 ‘따닥
    - 이미 결제 된 다시 결제 시도로 예상된 Balance와 PaymentHistory가 아니게 된다.
      ![Untitled 4](https://github.com/user-attachments/assets/1f8734f5-79c5-466d-a700-f808cce28f07)

        
    
- 해결 방법
    - 충분히 **낙관적 락**으로 풀기가 가능하다. 자원이 하나이기 때문에 여러 스레드 중 하나의 스레드만 성공해도 되기 때문에 나머지 요청들을 쳐내며 동시성을 제외하면 재시작 없이 낙관적 락을 풀 수 있게 된다.
        
        ```java
        try{
            return paymentFacade.payment(request.getUserId(), request.getReservationId());
        }catch (ObjectOptimisticLockingFailureException e){
            throw new CustomException(ErrorCode.NOT_AVAILABLE_SEAT);
        }
        ```
        
        ```java
        public class Reservation extends BaseEntity{
          @Version
          private long version;
        ```
      ![image](https://github.com/user-attachments/assets/6632c7a6-bdf3-4e2f-94da-30bec3521e9e)
