package com.hhp.concert.unitTest;


import com.hhp.concert.Business.Domain.User;
import com.hhp.concert.Business.Domain.WaitingQueue;
import com.hhp.concert.Business.service.JwtService;
import com.hhp.concert.Business.service.UserService;
import com.hhp.concert.application.WaitingFacade;
import com.hhp.concert.Business.service.QueueService;
import com.hhp.concert.Business.dto.GetTokenResponseDto;
import com.hhp.concert.Business.dto.GetWaitingTokenResponseDto;
import com.hhp.concert.util.CustomException;
import com.hhp.concert.util.ErrorCode;
import com.hhp.concert.util.enums.QueueKey;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Map;
import java.util.Optional;

import static org.mockito.BDDMockito.*;
import static org.junit.jupiter.api.Assertions.*;

public class WaitingFacadeTest {

    @Mock
    private QueueService queueService;

    @Mock
    private UserService userService;


    @Mock
    private JwtService jwtService;

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
        given(queueService.waitingQueueByUserId(userId)).willReturn(Optional.empty());
        given(jwtService.createProcessingToken(userId)).willReturn(token);

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
        given(queueService.waitingQueueByUserId(userId)).willReturn(Optional.of(new WaitingQueue(queueId, userId)));
        given(userService.getUser(userId)).willReturn(Optional.of(new User(userId, token, 0)));
//        given(jwtUtil.generateWaitingToken(QueueType.WAITING.getStr(), data)).willReturn(token);

        GetTokenResponseDto response = waitingFacade.getToken(userId);

        assertEquals(response.getToken(), token);
    }

    @Test
    @DisplayName("토큰 발급 예외 테스트")
    public void getTokenExceptionTest(){
        Long userId = 1L;
        Map<String, Object> data = Map.of(QueueKey.USER_ID.getStr(), userId);
        given(jwtService.createProcessingToken(userId)).willThrow(new CustomException(ErrorCode.INVALID_JWT));

        CustomException exception = assertThrows(CustomException.class, () -> {
            waitingFacade.getToken(userId);
        });

        assertEquals(exception.getMsg(), ErrorCode.INVALID_JWT.getMsg());
    }
    
    @Test
    @DisplayName("대기 번호 발급 테스트 - 대기열")
    public void getWaitingNumberWaitingStatusTest(){
        Long userId = 1L;
        String token = "token";
        Long waitingNumber = 5L;
        Map<String, Object> data = Map.of(QueueKey.USER_ID.getStr(), userId);
        given(jwtService.extractUserId(token)).willReturn(userId);
        given(queueService.getWaitingNumber(userId)).willReturn(waitingNumber);

        GetWaitingTokenResponseDto response = waitingFacade.getWaitingInfo(userId);

        assertEquals(response.getWaitingNumber(), waitingNumber);
        assertFalse(response.isProcessing());
    }

    @Test
    @DisplayName("대기 번호 발급 테스트 - 처리열")
    public void getWaitingNumberProcessingStatusTest(){
        Long userId = 1L;
        String token = "token";
        Long waitingNumber = 0L;
        given(jwtService.extractUserId(token)).willReturn(userId);
        given(queueService.getWaitingNumber(userId)).willReturn(waitingNumber);

        GetWaitingTokenResponseDto response = waitingFacade.getWaitingInfo(userId);

        assertEquals(response.getWaitingNumber(), waitingNumber);
        assertTrue(response.isProcessing());
    }


    
}
