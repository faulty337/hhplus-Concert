package com.hhp.concert.unitTest;


import com.hhp.concert.Business.Domain.User;
import com.hhp.concert.Business.Domain.WaitingQueue;
import com.hhp.concert.Business.service.JwtService;
import com.hhp.concert.Business.service.UserService;
import com.hhp.concert.application.WaitingFacade;
import com.hhp.concert.Business.service.QueueService;
import com.hhp.concert.Business.dto.GetWaitingTokenResponseDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

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
    @DisplayName("대기 번호 발급 테스트")
    public void getWaitingNumberWaitingStatusTest(){
        Long userId = 1L;
        String token = "token";
        Long waitingNumber = 5L;
        given(userService.getUser(userId)).willReturn(Optional.of(new User(userId, token, 1000)));
        given(queueService.waitingQueueByUserId(userId)).willReturn(Optional.of(new WaitingQueue(userId)));
        given(queueService.addWaiting(any(WaitingQueue.class))).willReturn(new WaitingQueue(userId));
        given(queueService.getWaitingNumber(userId)).willReturn(waitingNumber);

        GetWaitingTokenResponseDto response = waitingFacade.getWaitingInfo(userId);

        assertEquals(response.getWaitingNumber(), waitingNumber);
        assertFalse(response.isProcessing());
    }
    
}
