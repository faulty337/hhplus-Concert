package com.hhp.concert.unitTest;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

import com.hhp.concert.Business.Domain.ProcessQueue;
import com.hhp.concert.Business.Domain.WaitingQueue;
import com.hhp.concert.Business.Repository.ProcessQueueRepository;
import com.hhp.concert.Business.Repository.WaitingRepository;
import com.hhp.concert.Business.service.JwtService;
import com.hhp.concert.Business.service.waitingServiceImpl;
import com.hhp.concert.util.exception.CustomException;
import com.hhp.concert.util.exception.ErrorCode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

class QueueServiceTest {

    @Mock
    private WaitingRepository waitingRepository;

    @Mock
    private ProcessQueueRepository processQueueRepository;

    @Mock
    private JwtService jwtService;

    @InjectMocks
    private waitingServiceImpl queueService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        ReflectionTestUtils.setField(queueService, "processingSize", 2);
    }

    @Test
    @DisplayName("waiting 조회 테스트")
    public void getWaitingQueueTest() {
        Long waitingId = 1L;
        Long userId = 1L;
        given(waitingRepository.findByUserId(userId)).willReturn(Optional.of(new WaitingQueue(waitingId, userId)));

        Optional<WaitingQueue> returnData = queueService.waitingQueueByUserId(userId);

        assertTrue(returnData.isPresent());
        assertEquals(returnData.get().getId(), waitingId);
        assertEquals(returnData.get().getUserId(), userId);
    }

    @Test
    @DisplayName("waiting 조회 예외 테스트")
    public void getWaitingQueueExceptionTest() {
        Long userId = 1L;
        given(waitingRepository.findByUserId(userId)).willReturn(Optional.empty());

        CustomException exception = assertThrows(CustomException.class, () -> {
            queueService.getWaitingNumber(userId);
        });

        assertEquals(ErrorCode.NOT_FOUND_USER_ID.getMsg(), exception.getMsg());
    }

    @Test
    @DisplayName("대기 번호 조회 테스트")
    public void getWaitingNumberTest() {
        Long firstWaitingId = 32L;
        Long firstUserId = 32L;
        Long waitingId = 64L;
        Long userId = 64L;

        given(waitingRepository.getFirst()).willReturn(Optional.of(new WaitingQueue(firstWaitingId, firstUserId)));
        given(waitingRepository.findByUserId(userId)).willReturn(Optional.of(new WaitingQueue(waitingId, userId)));

        Long waitingNumber = queueService.getWaitingNumber(userId);

        assertEquals(waitingNumber, waitingId - firstWaitingId);
    }

    @Test
    @DisplayName("스케줄러 테스트")
    void testUpdateQueueTest() {
        Long userId1 = 1L;
        Long userId2 = 2L;
        long userId3 = 3L;
        ProcessQueue processQueue1 = new ProcessQueue(userId1, "validToken1");
        ProcessQueue processQueue2 = new ProcessQueue(userId2, "invalidToken");
        List<ProcessQueue> processQueueList = Arrays.asList(processQueue1, processQueue2);

        given(processQueueRepository.findAll()).willReturn(processQueueList);
        given(jwtService.validateToken("validToken1", userId1)).willReturn(true);
        given(jwtService.validateToken("invalidToken", userId2)).willReturn(false);

        WaitingQueue waitingQueue = new WaitingQueue(1L, userId3);
        given(waitingRepository.count()).willReturn(1L);
        given(waitingRepository.findById(1L)).willReturn(Optional.of(waitingQueue));
        given(waitingRepository.findByUserId(userId2)).willReturn(Optional.empty());
        given(jwtService.createProcessingToken(userId3)).willReturn("newToken");

        queueService.updateQueue();

        verify(processQueueRepository, times(1)).delete(processQueue2);
        verify(processQueueRepository, times(1)).save(any(ProcessQueue.class));
//        verify(waitingRepository, times(1)).delete(any(WaitingQueue.class));
    }

    @Test
    @DisplayName("스케줄러 이동 테스트")
    void testUpdateQueueMoveTest() {
        Long userId1 = 1L;
        Long userId2 = 2L;
        long userId3 = 3L;
//        ProcessQueue processQueue1 = new ProcessQueue(userId1, "validToken1");
//        ProcessQueue processQueue2 = new ProcessQueue(userId2, "validToken1");
        List<ProcessQueue> processQueueList = new ArrayList<>();

        given(processQueueRepository.findAll()).willReturn(processQueueList);
//        when(jwtService.validateToken("validToken1", userId1)).thenReturn(true);
//        when(jwtService.validateToken("validToken1", userId2)).thenReturn(false);

        WaitingQueue waitingQueue = new WaitingQueue(1L, userId2);
        given(waitingRepository.count()).willReturn(2L);
        given(waitingRepository.findById(userId1)).willReturn(Optional.of(new WaitingQueue(1L, userId1)));
        given(waitingRepository.findById(userId2)).willReturn(Optional.of(new WaitingQueue(2L, userId2)));
        given(waitingRepository.findByUserId(userId1)).willReturn(Optional.of(waitingQueue));
        given(waitingRepository.findByUserId(userId2)).willReturn(Optional.of(waitingQueue));
        given(jwtService.createProcessingToken(userId1)).willReturn("newToken");
        given(jwtService.createProcessingToken(userId2)).willReturn("newToken");

        queueService.updateQueue();

//        verify(processQueueRepository, times(1)).delete(processQueue2);
        verify(processQueueRepository, times(2)).save(any(ProcessQueue.class));
        verify(waitingRepository, times(2)).delete(any(WaitingQueue.class));
    }
}
