package com.hhp.concert.util.exception;

public record ErrorResponse(
        int statusCode,
        String msg
) {
}
