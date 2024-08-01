package com.hhp.concert.unitTest;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

import com.hhp.concert.Business.Domain.User;
import com.hhp.concert.Business.Repository.RedisRepository;
import com.hhp.concert.Business.Repository.UserRepository;
import com.hhp.concert.Business.service.JwtService;
import com.hhp.concert.Business.service.waitingServiceImpl;
import com.hhp.concert.util.enums.QueueType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Optional;

class QueueServiceTest {

    @Mock
    private RedisRepository redisRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private JwtService jwtService;

    @InjectMocks
    private waitingServiceImpl waitingService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        ReflectionTestUtils.setField(waitingService, "processingSize", 2);
    }

    @Test
    @DisplayName("waiting 조회 테스트")
    public void getWaitingQueueTest() {
        Long userId = 1L;
        Long rank = 3L;
        given(redisRepository.getSortedSetRank(any(String.class), any(String.class))).willReturn(rank);

        Long result = waitingService.getWaitingNumber(userId);

        assertEquals(rank, result);
    }
    @Test
    @DisplayName("waiting 조회 저장 테스트")
    public void getWaitingQueueSaveTest() {
        Long userId = 1L;
        Long rank = 3L;
        given(redisRepository.getSortedSetRank(QueueType.WAITING.getStr(), userId.toString())).willReturn(null);

        Long result = waitingService.getWaitingNumber(userId);

        verify(redisRepository, times(1)).addElementSortedSet(anyString(), anyString(), anyDouble());
    }


    @Test
    void testUpdateQueue() {
        when(redisRepository.getSortedSetSize(QueueType.PROCESSING.getStr())).thenReturn(5L);
        when(redisRepository.getFirstElement(QueueType.PROCESSING.getStr())).thenReturn("token1", (String) null);
        when(jwtService.isExpiredToken("token1")).thenReturn(false);
        doNothing().when(redisRepository).removeElementSortedSet(any(), any());

        waitingService.updateQueue();

        verify(redisRepository, times(1)).getSortedSetSize(QueueType.PROCESSING.getStr());
        verify(redisRepository, times(2)).getFirstElement(QueueType.PROCESSING.getStr());
        verify(jwtService, times(1)).isExpiredToken("token1");
        verify(redisRepository, times(1)).removeElementSortedSet(QueueType.PROCESSING.getStr(), "token1");
    }

    @Test
    void testMoveToProcessingQueue() {
        when(redisRepository.getFirstElement(QueueType.WAITING.getStr())).thenReturn("token1");
        doNothing().when(redisRepository).removeElementSortedSet(any(), any());
        doNothing().when(redisRepository).addElementSortedSet(any(), any(), anyDouble());

        waitingService.moveToProcessingQueue();

        verify(redisRepository, times(1)).getFirstElement(QueueType.WAITING.getStr());
        verify(redisRepository, times(1)).removeElementSortedSet(QueueType.WAITING.getStr(), "token1");
        verify(redisRepository, times(1)).addElementSortedSet(anyString(), anyString(), anyDouble());
    }

    @Test
    void testMoveUserToProcessingQueue() {
        Long userId = 1L;
        String token = "token";
        User user = new User(token, 19099);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(redisRepository.isElementInSortedSet(QueueType.PROCESSING.getStr(), token)).thenReturn(true);
        when(redisRepository.getFirstElement(QueueType.WAITING.getStr())).thenReturn("waitingToken1");

        waitingService.moveUserToProcessingQueue(userId);

        verify(userRepository, times(1)).findById(userId);
        verify(redisRepository, times(1)).isElementInSortedSet(QueueType.PROCESSING.getStr(), token);
        verify(redisRepository, times(1)).getFirstElement(QueueType.WAITING.getStr());
        verify(redisRepository, times(1)).removeElementSortedSet(QueueType.PROCESSING.getStr(), token);
        verify(redisRepository, times(1)).addElementSortedSet(anyString(), anyString(), anyDouble());
    }
}
