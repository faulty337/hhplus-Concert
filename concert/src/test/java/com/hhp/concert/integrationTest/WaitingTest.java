package com.hhp.concert.integrationTest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hhp.concert.Business.Domain.User;
import com.hhp.concert.Business.Repository.RedisRepository;
import com.hhp.concert.Business.service.JwtService;
import com.hhp.concert.Business.service.WaitingService;
import com.hhp.concert.Infrastructure.DBRepository.user.UserJpaRepository;
import com.hhp.concert.util.EmbeddedRedisConfig;
import com.hhp.concert.util.TestDatabaseManager;
import com.hhp.concert.util.JwtUtil;
import com.hhp.concert.util.enums.QueueType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import redis.embedded.RedisServer;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class WaitingTest {

    @Autowired
    private EmbeddedRedisConfig redisConfig;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Mock
    private JwtService jwtService;

    @Autowired
    private UserJpaRepository userJpaRepository;

    @Autowired
    private TestDatabaseManager testDatabaseManager;

    @Autowired
    private WaitingService waitingService;

    @Autowired
    private RedisRepository redisRepository;

    @BeforeEach
    public void setUp(){
        testDatabaseManager.execute();
        ReflectionTestUtils.setField(waitingService, "processingSize", 2L); // 필드 값 설정
        redisConfig.initializeRedis();
    }



    @Test
    @DisplayName("대기번호 테스트 테스트")
    public void getWaitingNumber() throws Exception {
        User user1 = userJpaRepository.save(new User(null, 0));
        User user2 = userJpaRepository.save(new User(null, 0));
        User user3 = userJpaRepository.save(new User(null, 0));


        mockMvc.perform(get("/concert/waiting/status").param("userId", String.valueOf(user1.getId())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.processing").value(true))
                .andReturn();
        mockMvc.perform(get("/concert/waiting/status").param("userId", String.valueOf(user2.getId())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.processing").value(true))
                .andReturn();
        mockMvc.perform(get("/concert/waiting/status").param("userId", String.valueOf(user3.getId())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.processing").value(false))
                .andReturn();


    }

    @Test
    @DisplayName("스케줄러 이동테스트")
    public void SchedulerMoveTest() throws Exception {
        Long userId1 = 1L;
        long userId2 = 2L;
        long userId3 = 3L;
        long userId4 = 4L;
        when(jwtService.isExpiredToken(anyString())).thenReturn(true);
        redisRepository.addElementSortedSet(QueueType.PROCESSING.getStr(), "token", System.currentTimeMillis());

        redisRepository.addElementSortedSet(QueueType.WAITING.getStr(), Long.toString(userId2), System.currentTimeMillis());
        redisRepository.addElementSortedSet(QueueType.WAITING.getStr(), Long.toString(userId3), System.currentTimeMillis());
        redisRepository.addElementSortedSet(QueueType.WAITING.getStr(), Long.toString(userId4), System.currentTimeMillis());

        waitingService.updateQueue();

        assertEquals(2, redisRepository.getSortedSetSize(QueueType.PROCESSING.getStr()));
    }

    @Test
    @DisplayName("스케줄러 삭제 테스트")
    public void SchedulerRemoveTest() throws Exception {
        when(jwtService.isExpiredToken(anyString())).thenReturn(false);
        redisRepository.addElementSortedSet(QueueType.PROCESSING.getStr(), "token", System.currentTimeMillis());

        waitingService.updateQueue();

        assertEquals(0, redisRepository.getSortedSetSize(QueueType.PROCESSING.getStr()));
    }

}
