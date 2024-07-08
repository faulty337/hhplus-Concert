package com.hhp.concert.e2eTest;

import com.hhp.concert.Business.Domain.Concert;
import com.hhp.concert.Business.Domain.ConcertSession;
import com.hhp.concert.Infrastructure.ConcertJpaRepository;
import com.hhp.concert.Infrastructure.ConcertSessionJpaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@SpringBootTest
@AutoConfigureMockMvc
public class ConcertTest {

    @Autowired
    private ConcertJpaRepository concertJpaRepository;

    @Autowired
    private ConcertSessionJpaRepository concertSessionJpaRepository;

    @Autowired
    private MockMvc mockMvc;

    @BeforeEach
    public void setUp(){
        concertJpaRepository.deleteAll();
        concertSessionJpaRepository.deleteAll();
    }

    @Test
    @DisplayName("좌석 반환")
    public void getSessionDateTest() throws Exception {
        Concert concert = concertJpaRepository.save(new Concert("test"));
        long listSize = 5L;
        List<ConcertSession> sessionList = new ArrayList<>();
        for(int i = 1; i <= listSize; i++){
            sessionList.add(new ConcertSession(LocalDateTime.now().plusDays(i), concert));
        }
        sessionList.add(new ConcertSession(LocalDateTime.now().minusDays(1), concert));

        concertSessionJpaRepository.saveAll(sessionList);

        mockMvc.perform(get("/concert/{concertId}/session", concert.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(listSize))
                .andDo(print())
                .andReturn();

    }




}
