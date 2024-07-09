package com.hhp.concert.util;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ErrorCode {
    NOT_FOUND_USER_ID(404, "userId를 찾을 수 없습니다."),
    EXPIRED_JWT(1001, "만료된 토큰입니다."),
    UNSUPPORTED_JWT(1002, "지원하지 않는 토큰입니다."),
    INVALID_JWT(1003, "잘못된 토큰입니다."),
    NOT_FOUND_CONCERT_ID(404, "concertId를 찾을 수 없습니다."),
    INVALID_SESSION_ID(400, "잘못된 sessionId 입니다."),
    NOT_FOUND_SEAT_ID(404, "seatId를 찾을 수 없습니다."), NOT_AVAILABLE_SEAT(409, "예약할 수 없는 좌석입니다.");



    private final int status;
    private final String msg;

    ErrorCode(int status, String msg) {
        this.status = status;
        this.msg = msg;
    }
}
