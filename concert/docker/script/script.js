import http from 'k6/http';
import { check, sleep } from 'k6';
import { Trend } from 'k6/metrics';
import { randomIntBetween } from 'https://jslib.k6.io/k6-utils/1.2.0/index.js';

const waitingTime = new Trend('waiting_time', true);
const sessionTime = new Trend('session_time', true);
const seatTime = new Trend('seat_time', true);
const reservationTime = new Trend('reservation_time', true);
const paymentTime = new Trend('payment_time', true);
const balanceTime = new Trend('balance_time', true);

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
    let concertId = randomIntBetween(1, 10);
    let userId = randomIntBetween(1, 10);

    // 1. 콘서트 대기 정보 가져오기
    let waitingResponse = http.get(`${BASE_URL}/concert/waiting/status?userId=${userId}`);
    waitingTime.add(waitingResponse.timings.duration);
    check(waitingResponse, { 'getSessionDate status was 200': (r) => r.status === 200 });
    let waitinginfo = JSON.parse(waitingResponse.body);
    while (!waitinginfo.processing) {
        waitingResponse = http.get(`${BASE_URL}/concert/waiting/status?userId=${userId}`);
        waitinginfo = JSON.parse(waitingResponse.body);
        sleep(1);
    }

    let token = waitinginfo.token;

    // 2. 세션 정보 가져오기
    let sessionResponse = http.get(`${BASE_URL}/concert/${concertId}/session`);
    sessionTime.add(sessionResponse.timings.duration);
    check(sessionResponse, { 'getSessionDate status was 200': (r) => r.status === 200 });

    let sessions = JSON.parse(sessionResponse.body);
    let sessionId;
    if (sessions.length > 0) {
        let randomIndex = Math.floor(Math.random() * sessions.length);
        sessionId = sessions[randomIndex].sessionId;
    } else {
        console.log('No sessions available');
        return;
    }

    // 3. 좌석 정보 가져오기
    let seatResponse = http.get(`${BASE_URL}/concert/${concertId}/seat?sessionId=${sessionId}`);
    seatTime.add(seatResponse.timings.duration); // seatTime에 기록
    check(seatResponse, { 'getSessionSeat status was 200': (r) => r.status === 200 });

    let seats = JSON.parse(seatResponse.body).seatList;
    let seatId = getRandomAvailableSeat(seats);

    // 4. 예약 요청
    let reservationPayload = JSON.stringify({
        concertId: concertId,
        sessionId: sessionId,
        seatId: seatId,
        userId: userId,
    });

    let reservationResponse = http.post(`${BASE_URL}/concert/reservation`, reservationPayload, {
        headers: {
            'Content-Type': 'application/json',
            'AuthorizationWaiting': `Bearer ${token}`,
        },
    });
    reservationTime.add(reservationResponse.timings.duration);
    check(reservationResponse, { 'reserveConcert status was 200': (r) => r.status === 200 });
    let reservationId = JSON.parse(reservationResponse.body).reservationId;

    // 5. 결제 요청
    let paymentPayload = JSON.stringify({
        userId: userId,
        reservationId: reservationId,
    });

    let paymentResponse = http.post(`${BASE_URL}/concert/payment`, paymentPayload, {
        headers: {
            'Content-Type': 'application/json',
            'AuthorizationWaiting': `Bearer ${token}`,
        },
    });
    paymentTime.add(paymentResponse.timings.duration);
    check(paymentResponse, { 'concertPayment status was 200': (r) => r.status === 200 });

    // 6. 잔액 확인 요청
    let balanceResponse = http.get(`${BASE_URL}/concert/balance?userId=${userId}`);
    balanceTime.add(balanceResponse.timings.duration);
    check(balanceResponse, { 'getBalance status was 200': (r) => r.status === 200 });

    sleep(1);
}
