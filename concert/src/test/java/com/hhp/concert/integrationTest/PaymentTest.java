package com.hhp.concert.integrationTest;



import com.fasterxml.jackson.databind.ObjectMapper;
import com.hhp.concert.Business.Domain.*;
import com.hhp.concert.Business.dto.PaymentRequestDto;
import com.hhp.concert.Infrastructure.concert.ConcertJpaRepository;
import com.hhp.concert.Infrastructure.payment.PaymentHistoryJpaRepository;
import com.hhp.concert.Infrastructure.processQueue.ProcessQueueJpaRepository;
import com.hhp.concert.Infrastructure.reservation.ReservationJpaRepository;
import com.hhp.concert.Infrastructure.seat.ConcertSeatJpaRepository;
import com.hhp.concert.Infrastructure.session.ConcertSessionJpaRepository;
import com.hhp.concert.Infrastructure.user.UserJpaRepository;
import com.hhp.concert.Infrastructure.waitingQueue.WaitingQueueJpaRepository;
import com.hhp.concert.util.JwtUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class PaymentTest {

    private static final Logger log = LoggerFactory.getLogger(PaymentTest.class);

    @Autowired
    private MockMvc mvc;
    @Autowired
    private UserJpaRepository userJpaRepository;
    @Autowired
    private WaitingQueueJpaRepository waitingQueueJpaRepository;
    @Autowired
    private ProcessQueueJpaRepository processQueueJpaRepository;
    @Autowired
    private ReservationJpaRepository reservationJpaRepository;
    @Autowired
    private ConcertJpaRepository concertJpaRepository;
    @Autowired
    private ConcertSessionJpaRepository concertSessionJpaRepository;
    @Autowired
    private ConcertSeatJpaRepository concertSeatJpaRepository;
    @Autowired
    private PaymentHistoryJpaRepository paymentHistoryJpaRepository;
    @MockBean
    private JwtUtil jwtUtil;
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    public void before() {
        reservationJpaRepository.deleteAll();
        concertSeatJpaRepository.deleteAll();
        concertSessionJpaRepository.deleteAll();
        concertJpaRepository.deleteAll();
        waitingQueueJpaRepository.deleteAll();
        processQueueJpaRepository.deleteAll();

    }


    @Test
    @DisplayName("결제 - 성공 테스트")
    public void paymentTest() throws Exception{
        User user = userJpaRepository.save(new User("" , 10000));
        Concert concert = concertJpaRepository.save(new Concert("concertTitle"));
        ConcertSession session  = concertSessionJpaRepository.save(new ConcertSession(LocalDateTime.now().plusHours(1), concert));
        List<ConcertSeat> concertSeatList = new ArrayList<>();

        long listSize = 5L;
        int i = 1;
        for(; i <= listSize; i++){
            concertSeatList.add(new ConcertSeat( i, 1000, false, session));
        }
        int seatNumber = i;
        ConcertSeat concertSeat = new ConcertSeat(seatNumber, 1000, true, session);
        concertSeatList.add(concertSeat);
        concertSeatJpaRepository.saveAll(concertSeatList);
        Reservation reservation = reservationJpaRepository.save(new Reservation(user, session, concertSeat, concertSeat.getPrice()));

        PaymentRequestDto requestDto = new PaymentRequestDto(user.getId(), reservation.getId());


        String validToken = "valid-jwt-token";

        when(jwtUtil.validateToken(validToken)).thenReturn(true);
        when(jwtUtil.extractSign(validToken)).thenReturn(String.valueOf(user.getId()));

        MvcResult mvcResult = mockMvc.perform(post("/concert/payment")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(JwtUtil.AUTHORIZATION_HEADER, "Bearer " + validToken)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isOk())
                .andDo(print())
                .andReturn();




    }
}
