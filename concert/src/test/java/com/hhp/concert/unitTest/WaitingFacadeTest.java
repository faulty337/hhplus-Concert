package com.hhp.concert.unitTest;


import com.hhp.concert.Business.Domain.User;
import com.hhp.concert.Business.Domain.WaitingQueue;
import com.hhp.concert.Business.UserService;
import com.hhp.concert.application.WaitingFacade;
import com.hhp.concert.Business.WaitingService;
import com.hhp.concert.Business.dto.GetTokenResponseDto;
import com.hhp.concert.Business.dto.GetWaitingTokenResponseDto;
import com.hhp.concert.util.CustomException;
import com.hhp.concert.util.ErrorCode;
import com.hhp.concert.util.JwtUtil;
import com.hhp.concert.util.enums.QueueKey;
import com.hhp.concert.util.enums.QueueType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;

import static org.mockito.BDDMockito.*;
import static org.junit.jupiter.api.Assertions.*;

public class WaitingFacadeTest {

    @Mock
    private WaitingService waitingService;

    @Mock
    private UserService userService;

    @Mock
    private JwtUtil jwtUtil;

    @InjectMocks
    private WaitingFacade waitingFacade;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);  // Mock 객체 초기화
    }

    @Test
    @DisplayName("새로운 토큰 발급 성공 테스트")
    public void getNewTokenTest(){
        Long userId = 1L;
        String token = "token";
        Map<String, Object> data = Map.of(QueueKey.USER_ID.getStr(), userId);
        given(waitingService.findByUserId(userId)).willReturn(Optional.empty());
        given(jwtUtil.generateWaitingToken(QueueType.WAITING.getStr(), data)).willReturn(token);

        GetTokenResponseDto response = waitingFacade.getToken(userId);

        assertEquals(response.getToken(), token);
    }

    @Test
    @DisplayName("기존 토큰 발급 성공 테스트")
    public void getTokenTest(){
        Long userId = 1L;
        Long queueId = 1L;
        String token = "token";
        Map<String, Object> data = Map.of(QueueKey.USER_ID.getStr(), userId);
        given(waitingService.findByUserId(userId)).willReturn(Optional.of(new WaitingQueue(queueId, userId)));
        given(userService.getUser(userId)).willReturn(Optional.of(new User(userId, token, 0)));
//        given(jwtUtil.generateWaitingToken(QueueType.WAITING.getStr(), data)).willReturn(token);

        GetTokenResponseDto response = waitingFacade.getToken(userId);

        assertEquals(response.getToken(), token);
    }

    @Test
    @DisplayName("토큰 발급 예외 테스트")
    public void getTokenExceptionTest(){
        Long userId = 1L;
        String token = "token";
        Map<String, Object> data = Map.of(QueueKey.USER_ID.getStr(), userId);
        given(jwtUtil.generateWaitingToken(QueueType.WAITING.getStr(), data)).willThrow(new CustomException(ErrorCode.INVALID_JWT));

        CustomException exception = assertThrows(CustomException.class, () -> {
            waitingFacade.getToken(userId);
        });

        assertEquals(exception.getMsg(), ErrorCode.INVALID_JWT.getMsg());
    }
    
    @Test
    @DisplayName("대기번호 발급 테스트 - 대기열")
    public void getWaitingNumberWaitingStatusTest(){
        Long userId = 1L;
        String token = "token";
        Long waitingNumber = 5L;
        Map<String, Object> data = Map.of(QueueKey.USER_ID.getStr(), userId);
        given(jwtUtil.extractData(token, QueueKey.USER_ID.getStr())).willReturn(String.valueOf(userId));
        given(waitingService.getWaitingNumber(userId)).willReturn(waitingNumber);

        GetWaitingTokenResponseDto response = waitingFacade.getWaitingInfo(token);

        assertEquals(response.getWaitingNumber(), waitingNumber);
        assertFalse(response.isProcessing());
    }

    @Test
    @DisplayName("대기번호 발급 테스트 - 처리열")
    public void getWaitingNumberProcessingStatusTest(){
        Long userId = 1L;
        String token = "token";
        Long waitingNumber = 0L;
        Map<String, Object> data = Map.of(QueueKey.USER_ID.getStr(), userId);
        given(jwtUtil.extractData(token, QueueKey.USER_ID.getStr())).willReturn(String.valueOf(userId));
        given(waitingService.getWaitingNumber(userId)).willReturn(waitingNumber);

        GetWaitingTokenResponseDto response = waitingFacade.getWaitingInfo(token);

        assertEquals(response.getWaitingNumber(), waitingNumber);
        assertTrue(response.isProcessing());
    }


    
}
