package com.hhp.concert.util.exception;

import lombok.Getter;

@Getter
public enum ErrorCode {
    NOT_FOUND_USER_ID(404, "userId를 찾을 수 없습니다."),
    EXPIRED_JWT(403, "만료된 토큰입니다."),
    UNSUPPORTED_JWT(403, "지원하지 않는 토큰입니다."),
    INVALID_JWT(403, "잘못된 토큰입니다."),
    NOT_FOUND_CONCERT_ID(404, "concertId를 찾을 수 없습니다."),
    NOT_FOUND_SESSION_ID(404, "sessionId를 찾을 수 없습니다."),
    NOT_FOUND_SEAT_NUMBER(404, "seatNumber를 찾을 수 없습니다."),
    NOT_AVAILABLE_SEAT(409, "예약할 수 없는 좌석입니다."),
    INVALID_TOKEN_STATE(403, "처리할 수 있는 토큰 상태가 아닙니다."),
    INVALID_AMOUNT(400, "잘못된 금액 입니다."),
    NOT_FOUND_RESERVATION_ID(404, "reservationId를 찾을 수 없습니다."),
    INSUFFICIENT_FUNDS(402, "잔액이 부족합니다."),
    IS_NOT_PROCESSING(403, "아직 처리 순서가 아닙니다."),
    NOT_AUTHORITY(403, "권한이 없습니다.");


    private final int status;
    private final String msg;

    ErrorCode(int status, String msg) {
        this.status = status;
        this.msg = msg;
    }
}
