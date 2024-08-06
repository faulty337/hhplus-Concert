package com.hhp.concert.cache;

import com.hhp.concert.Business.Domain.Concert;
import com.hhp.concert.Business.Domain.ConcertSession;
import com.hhp.concert.Business.service.ConcertSessionService;
import com.hhp.concert.Infrastructure.DBRepository.concert.ConcertJpaRepository;
import com.hhp.concert.Infrastructure.DBRepository.session.ConcertSessionJpaRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class CacheTest {
    @Autowired
    private ConcertJpaRepository concertJpaRepository;
    @Autowired
    private ConcertSessionJpaRepository concertSessionJpaRepository;
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private ConcertSessionService concertSessionService;


    @Test
    @DisplayName("날짜 반환 - 실행 시간 테스트")
    public void getSessionDateTimeTest() throws Exception {
        Concert concert = concertJpaRepository.save(new Concert("test"));
        long listSize = 5L;
        List<ConcertSession> concertSessionList = new ArrayList<>();
        for(int i = 1; i <= listSize; i++){
            concertSessionList.add(new ConcertSession(LocalDateTime.now().plusDays(i), concert.getId()));
        }
        concertSessionList.add(new ConcertSession(LocalDateTime.now().minusDays(1), concert.getId()));

        concertSessionJpaRepository.saveAll(concertSessionList);
        List<ConcertSession> concertSessions = concertSessionJpaRepository.findAllByConcertIdAndSessionTimeAfter(concert.getId(), LocalDateTime.now());
        when(concertSessionService.getSessionListByOpen(concert.getId())).thenReturn(concertSessions);


        for(int i = 0; i< 10; i++){
            mockMvc.perform(get("/concert/{concertId}/session", concert.getId()))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.size()").value(listSize))
                    .andReturn();
        }
        verify(concertSessionService, times(1)).getSessionListByOpen(concert.getId());
    }

}
