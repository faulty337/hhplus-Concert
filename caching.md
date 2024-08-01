# 캐싱 전략

## 캐싱

계산 결과, 응답 결과 등을 임시 저장소에 보관하여 이에 중복되는 요청, 계산에 대해 빠르게 처리할 수 있도록 하는 기술이다.

여기서 필요한 부분은 결과를 저장할 저장소와 결과가 유효성에서 중요 포인트를 갖는다.

## 저장소

저장소는 크게 로컬 캐시(Local Cache)와 글로벌 캐시(Global Cache)가 있다.

### 두 방식의 차이점

1. 접근 속도
    
    로컬 캐시 : 로컬 메모리이기 때문에 접근 속도가 매우 빠르다.
    
    글로벌 캐시 : 속도가 빠르긴 하지만 결국 네트워크 통신이기 때문에 로컬 캐시보다 느릴 수 있다.
    
2. 일관성
    
    단일 서버가 아닌 Scale-Out된 멀티 서버에서 각각의 서버가 캐시를 가지게 된다면 캐시 변경 시점의 공유가 필요하게 되어 일관 유지가 어렵게 된다. 때문에 글로벌 캐시와 같이 여러 서버가 하나의 저장소를 바라보는 글로벌 캐시에서 일관성 유지가 관리하기 편하다.
    

각각의 장단점이 있기 때문에 각 기능에 따라 효율적이게 사용해야 한다.

## 결과 유효성

크게 결과 유효성이라고 했지만 이에는 캐시 일관성, 캐시 적중률이 포함된다.

### 캐시 일관성

캐시된 결과가 실제 데이터와 일관 되는지를 말한다. 실제 데이터란, 실제 요청, 계산을 통한 결과와 캐시가 저장되어 있는 데이터가 맞는지를 말한다.

이러한 일관성은 캐시가 업데이트 되는 시점을 정확히 파악하고 업데이트 되어 유지되어야 한다.

### 캐시 적중률

정작 캐시를 집어놔도 해당 캐시를 사용할 요청, 계산이 들어오지 않으면 이에 대한 리소스는 낭비일 수밖에 없다.

**그렇다면 싹다 캐시를 집어놓으면 되지 않을까?**

당연히 그러면 안된다.

필요 없는 캐시 즉, hit 될 확률이 거의 없는 결과에 대해서는 이에 일관성 유지, 저장 공간에 있어서 모든 방면으로 낭비가 된다.

### 캐시 적용

캐시를 적용시켜야 기능에 대해 생각해야 한다.

1. Hit Rate 가 높은 기능에 대해서 생각해야 한다.
    - 단순 ‘기능이 많이 호출된다’가 아닌 ‘기능에 동일 응답이 많이 호출된다’ 이다.
2. 사용 리소스가 많은 기능에 써야 한다.
    - Index 조회, 단순 기능에 캐시는 효율이 좋지 않기 때문에 매 요청 시 효율이 좋지 않은 기능에 사용해야 한다.
    

### 슬로우 쿼리

- 오래 걸릴 수 있는 기능의 여러 요인은중 하나는 데이터베이스 쿼리에 있다.
- 슬로우 쿼리는 말 그대로 실행에 있어서 느린 쿼리 즉, 쿼리문 요청 이후 데이터 응답까지의 시간이 일정 시간 넘어갈 수 있는 쿼리를 말한다.
- 주 원인으로는
    - 비효율적인 인덱스
        - 인덱스 처리가 되지 않은 데이터와 인덱스 된 데이터의 조회 효율은 차원이 다르다.
        - 이때 대량의 데이터에서 인덱스가 아닌 특정 조건에 의해서만 조회하게 될 시 Full Table Scan이 일어나 느려질 수 있다.
    - 무분별한 Join
        - 여러 테이블을 Join할 시에 Join되는 테이블에 대한 필터링, 정렬 등을 수행하게 된다.
        - 마찬가지로 인덱스 처리로 성능을 향상 시킬 수 있다.
- 대부분의 조회 성능은 Index 처리로 해결 할 수 있다.
    - 이는 B-Tree, Hash와 같은 자료 구조를 이용하여 데이터 조회시 Index처리된 해당 컬럼에 있는 데이터를 이용해 인덱스 트리를 읽고 레코드 위치를 찾아 처리하게 된다. 이와 같이 진행되면 전체 데이터 조회가 logn
- 그러나 모든 조회 컬럼에 Index를 때려박았다가는 데이터 삽입, 수정 등에 있어서 성능이 저하될 수 있으며 추가 인덱스를 저장하는 공간이 필요하기에 많은 공간을 Index 유지에 사용할 수 있다.

## 쿼리 파악

### 쿼리 조회 시간

Concert - 약 1만

```jsx
concertJpaRepository.findById()
concertJpaRepository.existsById()
```

![Untitled](./image/Untitled.png)

Session - 약 10만

```jsx
concertSessionJpaRepository.findAllByConcertId()
concertSessionJpaRepository.findByIdAndConcertId()
concertSessionJpaRepository.findByIdAndConcertIdAndOpen()
```

![Untitled](./image/Untitled%201.png)

Seat - 약 500만

```jsx
seatJpaRepository.findById()
seatJpaRepository.findByIdAndSessionId()
seatJpaRepository.findAllBySessionId()
```

![Untitled](./image/Untitled%202.png)

Reservation - 약 500만

```jsx
reservationJpaRepository.findById()
reservationJpaRepository.findByIdAndUserId()
```

![Untitled](./image/Untitled%203.png)

`findById` 와 같은 Index 조회에 대한 쿼리는 보통 10ms 이내에 끝나게 된다.

하지만 index조회가 아닌 부분에서는 오래 걸리는 것을 볼 수 있다.

### Seat 조회

`seatJpaRepository.findAllBySessionId()`

- 해당 쿼리가 사용되는 로직은 현재 Session에 해당하는 모든 좌석에 대한 조회이다.
- 좌석에 대한 데이터는 예약시 해당 데이터를 직접 건드리기에 수정이 수시로 일어날 수 있는 부분이다.
- 즉, 누군가 조회 이후 예약을 한다면 최악의 경우 캐시 저장 → 캐시 업데이트 가 로직상 다분히 일어 날 수 있기에 캐시 사용에 있어 매리트가 없다.
- 현재 좌석에 예약에 대한 로직을 바꾸지 않는 이상 조회 부분에 있어서 캐시 이용으로 성능 향상을 기대할 수는 없다.

### Session 조회

`concertSessionJpaRepository.findAllByConcertId()`

- Session의 경우 Concert에 대한 세션 정보이기 때문에 수정 로직이 없다.
- 때문에 캐시 이용시 업데이트 로직이 없게 된다.
- 서비스 로직 상 콘서트에 대한 Session을 조회하기에 예약시 필수로 접근해야 하기 때문에 캐시 적용에 있어 성능 향상을 기대할 수 있다.

![Untitled](./image/Untitled%204.png)

![Untitled](./image/Untitled%205.png)

![Untitled](./image/Untitled%206.png)