package com.hhp.concert.unitTest;

import com.hhp.concert.Business.service.JwtService;
import com.hhp.concert.util.JwtUtil;
import com.hhp.concert.util.enums.QueueKey;
import com.hhp.concert.util.enums.QueueType;
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
    @DisplayName("토큰 제작 이후 데이터 확인 테스트")
    public void createAndExtractTest(){
        Long userId = 3L;
        String token = jwtUtil.generateNotExpirationToken(QueueType.WAITING.getStr(), Map.of(QueueKey.USER_ID.getStr(), String.valueOf(userId)));

        Long responseUserId = Long.valueOf((String) jwtUtil.extractData(token, QueueKey.USER_ID.getStr()));

        assertEquals(userId, responseUserId);

    }



}
