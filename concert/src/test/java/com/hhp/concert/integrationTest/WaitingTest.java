package com.hhp.concert.integrationTest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hhp.concert.Business.Domain.User;
import com.hhp.concert.Business.service.WaitingService;
import com.hhp.concert.Infrastructure.DBRepository.user.UserJpaRepository;
import com.hhp.concert.util.TestDatabaseManager;
import com.hhp.concert.util.JwtUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class WaitingTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private UserJpaRepository userJpaRepository;

    @Autowired
    private TestDatabaseManager testDatabaseManager;

    @Autowired
    private WaitingService waitingService;

    @BeforeEach
    public void setUp(){
        testDatabaseManager.execute();
        ReflectionTestUtils.setField(waitingService, "processingSize", 2L); // 필드 값 설정
    }

//    @Test
//    @DisplayName("새로운 토큰 발급 테스트")
//    public void getTokenReissued() throws Exception {
//        User user = userJpaRepository.save(new User(null, 0));
//
//        mockMvc.perform(get("/concert/waiting/status").param("userId", String.valueOf(user.getId())))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.token").exists())
//                .andDo(print())
//                .andReturn();
//
//        MvcResult result = mockMvc.perform(get("/concert/waiting/status").param("userId", String.valueOf(user.getId())))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.token").exists())
//                .andDo(print())
//                .andReturn();
//
//        String jsonResponse = result.getResponse().getContentAsString();
//        JSONObject jsonObject = new JSONObject(jsonResponse);
//        String token = jsonObject.getString("token");
//    }
//
//    @Test
//    @DisplayName("새로운 토큰 발급 테스트")
//    public void getToken() throws Exception {
//        User user = userJpaRepository.save(new User(null, 0));
//
//        MvcResult result = mockMvc.perform(get("/concert/waiting/token").param("userId", String.valueOf(user.getId())))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.token").exists())
//                .andDo(print())
//                .andReturn();
//
//        String jsonResponse = result.getResponse().getContentAsString();
//        JSONObject jsonObject = new JSONObject(jsonResponse);
//        String token = jsonObject.getString("token");
//
//        user = userJpaRepository.findById(user.getId()).get();
//        int size = waitingQueueJpaRepository.findAll().size();
//
//        assertEquals(size, 1);
//        assertEquals(user.getWaitingToken(), token);
//
//    }


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


}
