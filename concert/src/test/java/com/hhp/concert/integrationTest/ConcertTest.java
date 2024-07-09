package com.hhp.concert.integrationTest;

import com.hhp.concert.Business.Domain.Concert;
import com.hhp.concert.Business.Domain.ConcertSeat;
import com.hhp.concert.Business.Domain.ConcertSession;
import com.hhp.concert.Infrastructure.ConcertJpaRepository;
import com.hhp.concert.Infrastructure.ConcertSeatJpaRepository;
import com.hhp.concert.Infrastructure.ConcertSessionJpaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

    private static final Logger log = LoggerFactory.getLogger(ConcertTest.class);
    @Autowired
    private ConcertJpaRepository concertJpaRepository;

    @Autowired
    private ConcertSessionJpaRepository concertSessionJpaRepository;
    @Autowired
    private ConcertSeatJpaRepository concertSeatJpaRepository;

    @Autowired
    private MockMvc mockMvc;

    @BeforeEach
    public void setUp(){
        concertJpaRepository.deleteAll();
        concertSessionJpaRepository.deleteAll();
        concertSeatJpaRepository.deleteAll();
    }

    @Test
    @DisplayName("날짜 반환")
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

    @Test
    @DisplayName("날짜 반환")
    public void getSessionSeatTest() throws Exception {
        Concert concert = concertJpaRepository.save(new Concert("test"));
        ConcertSession concertSession = concertSessionJpaRepository.save(new ConcertSession(LocalDateTime.now().minusDays(1), concert));

        List<ConcertSeat> concertSeatList = new ArrayList<>();

        long listSize = 5L;
        for(int i = 1; i <= listSize; i++){
            concertSeatList.add(new ConcertSeat( i, 1000, false, concertSession));
        }

        concertSeatJpaRepository.saveAll(concertSeatList);

        mockMvc.perform(get("/concert/{concertId}/seat", concert.getId()).param("sessionId", String.valueOf(concertSession.getId())))
                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.date").value(concertSession.getSessionTime()))
                .andExpect(jsonPath("$.seatList.size()").value(listSize))
                .andDo(print())
                .andReturn();

    }




}
