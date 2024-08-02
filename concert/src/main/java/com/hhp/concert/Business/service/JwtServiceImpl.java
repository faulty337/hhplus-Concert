package com.hhp.concert.Business.service;

import com.hhp.concert.util.JwtUtil;
import com.hhp.concert.util.enums.QueueType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class JwtServiceImpl implements JwtService{
    private final JwtUtil jwtUtil;

    @Override
    public String createProcessingToken(Long userId) {
        Map<String, Object> data = Map.of("tokenType", "processing");
        return jwtUtil.generateToken(String.valueOf(userId), data, new Date(System.currentTimeMillis() + 1000 * 60 * 5));
    }

    @Override
    public Long extractUserId(String token) {
        String userId =jwtUtil.extractSign(token);
        return Long.valueOf(userId);
    }

    @Override
    public Boolean isProcessingToken(String token){
        String queueType = (String) jwtUtil.extractData(token, "tokenType");
        return queueType.equals(QueueType.PROCESSING.getStr());
    }

    @Override
    public Boolean isProcessing(String token, Long userId) {
        return Long.valueOf(jwtUtil.extractSign(token)).equals(userId);
    }

    @Override
    public Boolean validateToken(String token, Long userId) {
        boolean result;
        try{
            result = jwtUtil.extractSign(token).equals(String.valueOf(userId));
        }catch (Exception e){
            return false;
        }
        return result;
    }

    @Override
    public Boolean isExpiredToken(String token){
        return jwtUtil.validateToken(token);
    }


}
