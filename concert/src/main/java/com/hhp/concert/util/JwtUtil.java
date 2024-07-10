package com.hhp.concert.util;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Component
public class JwtUtil {

    @Value("${jwt.secret}")
    private String secret;

    private Key getSigningKey() {
        byte[] keyBytes = secret.getBytes();
        return Keys.hmacShaKeyFor(keyBytes);
    }

    public String generateNotExpirationToken(String sign) {
        Map<String, Object> claims = new HashMap<>();
        return generateNotExpirationToken(sign, claims);
    }

    public String generateProcessToken(String userId) {
        Map<String, Object> claims = new HashMap<>();
        return generateNotExpirationToken(userId, claims);
    }

    public String generateNotExpirationToken(String sign, Map<String, Object> claims) {
        Map<String, Object> mutableClaims = new HashMap<>(claims);
        return Jwts.builder()
                .setSubject(sign)
                .setClaims(mutableClaims)
                .setIssuedAt(new Date())
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    public String generateToken(String sign, Map<String, Object> claims, Date expiration) {
        Map<String, Object> mutableClaims = new HashMap<>(claims);
        return Jwts.builder()
                .setSubject(sign)
                .setClaims(mutableClaims)
                .setIssuedAt(new Date())
                .setExpiration(expiration)
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
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
