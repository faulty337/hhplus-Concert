package com.hhp.concert.unitTest;

import com.hhp.concert.Business.service.JwtService;
import com.hhp.concert.util.JwtUtil;
import com.hhp.concert.util.enums.QueueKey;
import com.hhp.concert.util.enums.QueueType;
import io.jsonwebtoken.Claims;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static org.mockito.BDDMockito.*;
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
