package com.hhp.concert.Business.service;

import com.hhp.concert.util.JwtUtil;
import com.hhp.concert.util.enums.QueueKey;
import com.hhp.concert.util.enums.QueueType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class JwtServiceImpl implements JwtService{
    private final JwtUtil jwtUtil;
    @Override
    public String createWaitingToken(Long userId) {
        Map<String, Object> data = Map.of(QueueKey.USER_ID.getStr(), String.valueOf(userId));
        return jwtUtil.generateNotExpirationToken(QueueType.WAITING.getStr(), data);
    }

    @Override
    public Long extractUserId(String token) {
        String userId = (String) jwtUtil.extractData(token, QueueKey.USER_ID.getStr());
        return Long.valueOf(userId);
    }

    @Override
    public Boolean isProcessingToken(String token){
        String queueType = jwtUtil.extractSign(token);
        return queueType.equals(QueueType.PROCESSING.getStr());
    }


}
