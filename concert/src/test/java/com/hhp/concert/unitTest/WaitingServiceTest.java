package com.hhp.concert.unitTest;


import com.hhp.concert.Business.Domain.WaitingQueue;
import com.hhp.concert.Business.WaitingService;
import com.hhp.concert.Business.WaitingServiceImpl;
import com.hhp.concert.Infrastructure.WaitingRepositoryImpl;
import com.hhp.concert.util.CustomException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.mockito.BDDMockito.*;
import static org.junit.jupiter.api.Assertions.*;


public class WaitingServiceTest {
    @Mock
    private WaitingRepositoryImpl waitingRepository;

    @InjectMocks
    private WaitingServiceImpl waitingService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);  // Mock 객체 초기화
    }

    @Test
    @DisplayName("waiting 조회 테스트")
    public void getWaitingQueueTest(){
        Long waitingId = 1L;
        Long userId = 1L;
        given(waitingRepository.findByUserId(userId)).willReturn(Optional.of(new WaitingQueue(waitingId, userId)));

        WaitingQueue returnData = waitingService.findByUserId(userId).get();

        assertEquals(returnData.getId(), waitingId);
        assertEquals(returnData.getUserId(), userId);
    }

    @Test
    @DisplayName("waiting 조회 테스트")
    public void getWaitingQueueExceptionTest(){
        Long waitingId = 1L;
        Long userId = 1L;
        given(waitingRepository.findByUserId(userId)).willReturn(Optional.empty());

        CustomException exception = assertThrows(CustomException.class, () -> {
            waitingService.findByUserId(userId);
        });
    }


    @Test
    @DisplayName("대기 번호 조회 테스트")
    public void getWaitingNumberTest(){
        Long firstWaitingId = 32L;
        Long firstUserId = 32L;

        Long waitingId = 64L;
        Long userId = 64L;

        given(waitingRepository.getFirst()).willReturn(Optional.of(new WaitingQueue(firstWaitingId, firstUserId)));
        given(waitingRepository.findByUserId(userId)).willReturn(Optional.of(new WaitingQueue(waitingId, userId)));

        Long waitingNumber = waitingService.getWaitingNumber(userId);

        assertEquals(waitingNumber, waitingId - firstWaitingId);

    }
}
