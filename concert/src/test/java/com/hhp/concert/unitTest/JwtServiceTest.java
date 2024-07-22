package com.hhp.concert.unitTest;

import com.hhp.concert.util.JwtUtil;
import io.jsonwebtoken.Claims;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Date;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class JwtServiceTest {

    @InjectMocks
    private JwtUtil jwtUtil;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        ReflectionTestUtils.setField(jwtUtil, "secret", "super-secret-key-for-hhp-application-2024!");// Mock 객체 초기화
    }


    @Test
    public void testTokenCreationAndExtraction() {
        Long userId = 123L;
        Map<String, Object> data = Map.of("tokenType", "processing");
        String token = jwtUtil.generateToken(String.valueOf(userId), data, new Date(System.currentTimeMillis() + 1000 * 60 * 5));

        System.out.println("Generated Token: " + token); // 디버깅용 출력

        Claims claims = jwtUtil.extractClaims(token);
        System.out.println("Claims: " + claims); // 디버깅용 출력

        String extractedUserId = jwtUtil.extractSign(token);

        System.out.println("Extracted User ID: " + extractedUserId); // 디버깅용 출력

        assertNotNull(extractedUserId);
        assertEquals(String.valueOf(userId), extractedUserId);
    }


}
