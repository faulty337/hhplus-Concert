package com.hhp.concert.Business.service;

public interface JwtService{
    String createWaitingToken(Long userId);

    Long extractUserId(String token);

    Boolean isProcessingToken(String token);

    Boolean isProcessing(String token, Long userId);
}
