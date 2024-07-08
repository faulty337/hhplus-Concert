package com.hhp.concert.util;

public record ErrorResponse(
        int statusCode,
        String msg
) {
}
