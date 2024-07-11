package com.hhp.concert.Business.service;

public interface JwtService{
    String createProcessingToken(Long userId);


    Long extractUserId(String token);

    Boolean isProcessingToken(String token);

    Boolean isProcessing(String token, Long userId);

    Boolean validateToken(String token, Long userId);
}
