package com.hhp.concert.util;

import io.jsonwebtoken.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Component
public class JwtUtil {

    @Value("${jwt.secret}")
    private String secret; // 변경 필요

    public String generateWaitingToken(String userId) {
        Map<String, Object> claims = new HashMap<>();
        return generateWaitingToken(userId, claims);
    }

    // data 파라미터를 사용하여 토큰 생성
    public String generateWaitingToken(String sign, Map<String, Object> claims) {
        return Jwts.builder()
                .setSubject(sign)
                .setClaims(claims)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 30)) // 30분 유효
                .signWith(SignatureAlgorithm.HS256, secret)
                .compact();
    }


    public Claims extractClaims(String token) {
        try {
            return Jwts.parser()
                    .setSigningKey(secret)
                    .parseClaimsJws(token)
                    .getBody();
        } catch (ExpiredJwtException e) {
            throw new CustomException(ErrorCode.EXPIRED_JWT);
        } catch (UnsupportedJwtException | IllegalArgumentException | SignatureException | MalformedJwtException e) {
            throw new CustomException(ErrorCode.INVALID_JWT);
        }
    }

    public boolean isTokenExpired(String token) {
        return extractClaims(token).getExpiration().before(new Date());
    }

    public String extractSign(String token) {
        return extractClaims(token).getSubject();
    }

    public String extractData(String token, String key){
        return (String) extractClaims(token).get(key);
    }
}