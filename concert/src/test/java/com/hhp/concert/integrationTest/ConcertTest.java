package com.hhp.concert.integrationTest;

import com.hhp.concert.Business.Domain.Concert;
import com.hhp.concert.Business.Domain.Seat;
import com.hhp.concert.Business.Domain.Session;
import com.hhp.concert.Infrastructure.ConcertJpaRepository;
import com.hhp.concert.Infrastructure.SeatJpaRepository;
import com.hhp.concert.Infrastructure.SessionJpaRepository;
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
    private SessionJpaRepository sessionJpaRepository;
    @Autowired
    private SeatJpaRepository seatJpaRepository;

    @Autowired
    private MockMvc mockMvc;

    @BeforeEach
    public void setUp(){
        seatJpaRepository.deleteAll();
        sessionJpaRepository.deleteAll();
        concertJpaRepository.deleteAll();

    }

    @Test
    @DisplayName("날짜 반환")
    public void getSessionDateTest() throws Exception {
        Concert concert = concertJpaRepository.save(new Concert("test"));
        long listSize = 5L;
        List<Session> sessionList = new ArrayList<>();
        for(int i = 1; i <= listSize; i++){
            sessionList.add(new Session(LocalDateTime.now().plusDays(i), concert));
        }
        sessionList.add(new Session(LocalDateTime.now().minusDays(1), concert));

        sessionJpaRepository.saveAll(sessionList);

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
        Session concertSession = sessionJpaRepository.save(new Session(LocalDateTime.now().minusDays(1), concert));

        List<Seat> seatList = new ArrayList<>();

        long listSize = 5L;
        for(int i = 1; i <= listSize; i++){
            seatList.add(new Seat( i, 1000, false, concertSession));
        }

        seatJpaRepository.saveAll(seatList);

        mockMvc.perform(get("/concert/{concertId}/seat", concert.getId()).param("sessionId", String.valueOf(concertSession.getId())))
                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.date").value(concertSession.getSessionTime()))
                .andExpect(jsonPath("$.seatList.size()").value(listSize))
                .andDo(print())
                .andReturn();

    }




}
