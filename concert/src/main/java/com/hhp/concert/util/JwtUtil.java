package com.hhp.concert.util;

import com.hhp.concert.util.exception.CustomException;
import com.hhp.concert.util.exception.ErrorCode;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Component
public class JwtUtil {

    public static final String AUTHORIZATION_HEADER = "AuthorizationWaiting";

    @Value("${jwt.secret}")
    private String secret;

    private static final Logger logger = LogManager.getLogger(JwtUtil.class);

    private Key getSigningKey() {
        byte[] keyBytes = secret.getBytes();
        return Keys.hmacShaKeyFor(keyBytes);
    }

    public String generateNotExpirationToken(String sign, Map<String, Object> claims) {
        Map<String, Object> mutableClaims = new HashMap<>(claims);
        return Jwts.builder()
                .setClaims(mutableClaims)
                .setSubject(sign)
                .setIssuedAt(new Date())
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    public String generateToken(String sign, Map<String, Object> claims, Date expiration) {
        Map<String, Object> mutableClaims = new HashMap<>(claims);
        return Jwts.builder()
                .setClaims(mutableClaims)
                .setSubject(sign)
                .setIssuedAt(new Date())
                .setExpiration(expiration)
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(getSigningKey())
                    .build()
                    .parseClaimsJws(token);
            return true;
        } catch (SecurityException | MalformedJwtException e) {
            logger.info("Invalid JWT signature, 유효하지 않는 JWT 서명 입니다.");
        } catch (ExpiredJwtException e) {
            logger.info("Expired JWT token, 만료된 JWT token 입니다.");
        } catch (UnsupportedJwtException e) {
            logger.info("Unsupported JWT token, 지원되지 않는 JWT 토큰 입니다.");
        } catch (IllegalArgumentException e) {
            logger.info("JWT claims is empty, 잘못된 JWT 토큰 입니다.");
        } catch (SignatureException e) {
            logger.info("신뢰할 수 없는 JWT 토큰입니다.");
        }
        return false;
    }

    public Claims extractClaims(String token) {
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(getSigningKey())
                    .build()
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

    public Object extractData(String token, String key) {
        return extractClaims(token).get(key);
    }
}
