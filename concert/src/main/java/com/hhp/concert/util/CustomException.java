package com.hhp.concert.util;


import lombok.Getter;

@Getter
public class CustomException extends RuntimeException {
    public CustomException(ErrorCode e) {
        this.statusCode = e.getStatus();
        this.msg = e.getMsg();
    }

    private int statusCode;
    private String msg;

    @Override
    public String getMessage() {
        return msg;
    }
}