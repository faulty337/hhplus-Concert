package com.hhp.concert.unitTest;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

import com.hhp.concert.Business.Domain.ProcessQueue;
import com.hhp.concert.Business.Domain.WaitingQueue;
import com.hhp.concert.Business.ProcessQueueRepository;
import com.hhp.concert.Business.Repository.WaitingRepository;
import com.hhp.concert.Business.service.JwtService;
import com.hhp.concert.Business.service.QueueServiceImpl;
import com.hhp.concert.util.CustomException;
import com.hhp.concert.util.ErrorCode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;

class QueueServiceTest {

    @Mock
    private WaitingRepository waitingRepository;

    @Mock
    private ProcessQueueRepository processQueueRepository;

    @Mock
    private JwtService jwtService;

    @InjectMocks
    private QueueServiceImpl queueService;

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
    void testUpdateQueue() {
        Long userId1 = 1L;
        Long userId2 = 2L;
        long userId3 = 3L;
        ProcessQueue processQueue1 = new ProcessQueue(userId1, "validToken1");
        ProcessQueue processQueue2 = new ProcessQueue(userId2, "invalidToken");
        ProcessQueue processQueue3 = new ProcessQueue(userId3, "validToken2");
        List<ProcessQueue> processQueueList = Arrays.asList(processQueue1, processQueue2);

        when(processQueueRepository.findAll()).thenReturn(processQueueList);
        when(jwtService.validateToken("validToken1", userId1)).thenReturn(true);
        when(jwtService.validateToken("invalidToken", userId2)).thenReturn(false);

        WaitingQueue waitingQueue = new WaitingQueue(1L, userId3);
        when(waitingRepository.findById(1L)).thenReturn(Optional.of(waitingQueue));
        when(jwtService.createProcessingToken(userId3)).thenReturn("newToken");

        queueService.updateQueue();

        verify(processQueueRepository, times(1)).delete(processQueue2);
        verify(processQueueRepository, times(1)).save(any(ProcessQueue.class));
        verify(waitingRepository, times(1)).deleteById(userId1);
    }
}
