import http from 'k6/http';
import { check, sleep } from 'k6';
import { randomIntBetween } from 'https://jslib.k6.io/k6-utils/1.2.0/index.js';

export let options = {
    vus: 100,
    duration: '10s',
    thresholds: {
        http_req_duration: ['avg<200', 'p(95)<300', 'max<500'],
    },
};

const BASE_URL = 'http://host.docker.internal:8080';


function getRandomAvailableSeat(seats) {
    let availableSeats = seats.filter(seat => seat.available);
    if (availableSeats.length === 0) {
        console.log('No available seats found');
        return null;
    }
    let randomIndex = Math.floor(Math.random() * availableSeats.length);
    return availableSeats[randomIndex].seatId;
}

export default function () {
    // 1. 콘서트 세션 정보를 가져오기
    let concertId = randomIntBetween(1, 10);
    let userId = randomIntBetween(1, 10);

    let waitingResponse = http.get(`${BASE_URL}/concert/waiting/status?userId=${userId}`)
    check(waitingResponse, { 'getSessionDate status was 200': (r) => r.status === 200 });
    let waitinginfo = JSON.parse(waitingResponse.body);
    while(!waitinginfo.processing){
        waitingResponse = http.get(`${BASE_URL}/concert/waiting/status?userId=${userId}`)
        waitinginfo = JSON.parse(waitingResponse.body);
        sleep(1000)
    }

    let token = waitinginfo.token

    let sessionResponse = http.get(`${BASE_URL}/concert/${concertId}/session`);
    check(sessionResponse, { 'getSessionDate status was 200': (r) => r.status === 200 });

    let sessions = JSON.parse(sessionResponse.body);
    let sessionId
    if (sessions.length > 0) {
        let randomIndex = Math.floor(Math.random() * sessions.length);
        sessionId = sessions[randomIndex].sessionId;
    } else {
        console.log('No sessions available');
        return;
    }

    // 2. 특정 세션의 좌석 정보 조회
    let seatResponse = http.get(`${BASE_URL}/concert/${concertId}/seat?sessionId=${sessionId}`);
    check(seatResponse, { 'getSessionSeat status was 200': (r) => r.status === 200 });

    let seats = JSON.parse(seatResponse.body).seatList;

    let seatId = getRandomAvailableSeat(seats);

    // 3. 예약 요청
    let reservationPayload = JSON.stringify({
        concertId: concertId,
        sessionId: sessionId,
        seatId: seatId,
        userId: userId
    });

    let reservationResponse = http.post(`${BASE_URL}/concert/reservation`, reservationPayload, {
        headers: {
            'Content-Type': 'application/json',
            'AuthorizationWaiting': `Bearer ${token}`
        },
    });

    let reservationSuccess = check(reservationResponse, { 'reserveConcert status was 200': (r) => r.status === 200 });
    if (!reservationSuccess) {
        console.log('reservation request body', reservationPayload)
        console.log(`Reservation failed for user ${userId} with response: ${reservationResponse.body}`);
    }
    let reservationId = JSON.parse(reservationResponse.body).reservationId;

    // 4. 결제 요청
    let paymentPayload = JSON.stringify({
        userId: userId,
        reservationId: reservationId
    });

    let paymentResponse = http.post(`${BASE_URL}/concert/payment`, paymentPayload, {
        headers: {
            'Content-Type': 'application/json',
            'AuthorizationWaiting': `Bearer ${token}`
        },

    });
    let paymentSuccess = check(paymentResponse, { 'concertPayment status was 200': (r) => r.status === 200 });
    if (!paymentSuccess) {
        console.log('payment request body', paymentPayload)
        console.log(`Payment failed for user ${userId} with response: ${paymentResponse.body}`);
    }

    // 5. 잔액 확인 요청
    let balanceResponse = http.get(`${BASE_URL}/concert/balance?userId=${userId}`);
    check(balanceResponse, { 'getBalance status was 200': (r) => r.status === 200 });

    sleep(1); // 각 가상 사용자마다 1초씩 쉬면서 작업 간의 간격을 둠
}
