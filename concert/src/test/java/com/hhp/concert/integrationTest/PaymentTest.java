package com.hhp.concert.integrationTest;



import com.fasterxml.jackson.databind.ObjectMapper;
import com.hhp.concert.Business.Domain.*;
import com.hhp.concert.Business.dto.ChargeRequestDto;
import com.hhp.concert.Business.dto.PaymentRequestDto;
import com.hhp.concert.Infrastructure.DBRepository.concert.ConcertJpaRepository;
import com.hhp.concert.Infrastructure.DBRepository.payment.PaymentHistoryJpaRepository;
import com.hhp.concert.Infrastructure.DBRepository.processQueue.ProcessQueueJpaRepository;
import com.hhp.concert.Infrastructure.DBRepository.reservation.ReservationJpaRepository;
import com.hhp.concert.Infrastructure.DBRepository.seat.ConcertSeatJpaRepository;
import com.hhp.concert.Infrastructure.DBRepository.session.ConcertSessionJpaRepository;
import com.hhp.concert.Infrastructure.DBRepository.user.UserJpaRepository;
import com.hhp.concert.Infrastructure.DBRepository.waitingQueue.WaitingQueueJpaRepository;
import com.hhp.concert.util.TestDatabaseManager;
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
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
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

    private String validToken = "valid-jwt-token";
    @Autowired
    private TestDatabaseManager testDatabaseManager;

    @BeforeEach
    public void setUp(){
        testDatabaseManager.execute();

    }

    @Test
    @DisplayName("충전 - 성공 테스트")
    public void chargeTest() throws Exception{
        int balance = 100000;
        User user = userJpaRepository.save(new User("", balance));
        int amount = 3000;
        ChargeRequestDto requestDto = new ChargeRequestDto(user.getId(), amount);
        MvcResult mvcResult = mockMvc.perform(patch("/concert/charge")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(JwtUtil.AUTHORIZATION_HEADER, "Bearer " + validToken)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isOk())
                .andDo(print())
                .andReturn();

        user = userJpaRepository.findById(user.getId()).get();

        assertEquals(balance + amount, user.getBalance());
    }

    @Test
    @DisplayName("결제 - 성공 테스트")
    public void paymentTest() throws Exception{
        User user = userJpaRepository.save(new User("" , 10000));
        Concert concert = concertJpaRepository.save(new Concert("concertTitle"));
        ConcertSession session  = concertSessionJpaRepository.save(new ConcertSession(LocalDateTime.now().plusHours(1), concert.getId()));
        List<ConcertSeat> concertSeatList = new ArrayList<>();

        long listSize = 5L;
        int i = 1;
        for(; i <= listSize; i++){
            concertSeatList.add(new ConcertSeat( i, 1000, false, session.getId()));
        }
        int seatNumber = i;
        ConcertSeat concertSeat = new ConcertSeat(seatNumber, 1000, true, session.getId());
        concertSeatList.add(concertSeat);
        concertSeatJpaRepository.saveAll(concertSeatList);
        Reservation reservation = reservationJpaRepository.save(new Reservation(user.getId(), session.getId(), concertSeat.getId(), concertSeat.getPrice()));

        PaymentRequestDto requestDto = new PaymentRequestDto(user.getId(), reservation.getId());




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
