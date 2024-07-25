package com.hhp.concert.integrationTest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hhp.concert.Business.Domain.*;
import com.hhp.concert.Business.dto.ReservationRequestDto;
import com.hhp.concert.Business.dto.ReservationResponseDto;
import com.hhp.concert.Infrastructure.concert.ConcertJpaRepository;
import com.hhp.concert.Infrastructure.reservation.ReservationJpaRepository;
import com.hhp.concert.Infrastructure.seat.ConcertSeatJpaRepository;
import com.hhp.concert.Infrastructure.session.ConcertSessionJpaRepository;
import com.hhp.concert.Infrastructure.user.UserJpaRepository;
import com.hhp.concert.util.JwtUtil;
import com.hhp.concert.util.exception.ErrorCode;
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
import java.util.Optional;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.junit.jupiter.api.Assertions.*;


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
    private ObjectMapper objectMapper;

    @MockBean
    private JwtUtil jwtUtil;
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private UserJpaRepository userJpaRepository;
    @Autowired
    private ReservationJpaRepository reservationJpaRepository;

    private final String validToken = "valid-jwt-token";
    @BeforeEach
    public void setUp(){
        reservationJpaRepository.deleteAll();
        concertSeatJpaRepository.deleteAll();
        concertSessionJpaRepository.deleteAll();
        concertJpaRepository.deleteAll();

        String validToken = "valid-jwt-token";

        when(jwtUtil.validateToken(validToken)).thenReturn(true);
        when(jwtUtil.extractSign(validToken)).thenReturn(String.valueOf(1L));
    }

    @Test
    @DisplayName("날짜 반환 - 성공 테스트")
    public void getSessionDateTest() throws Exception {
        Concert concert = concertJpaRepository.save(new Concert("test"));
        long listSize = 5L;
        List<ConcertSession> concertSessionList = new ArrayList<>();
        for(int i = 1; i <= listSize; i++){
            concertSessionList.add(new ConcertSession(LocalDateTime.now().plusDays(i), concert));
        }
        concertSessionList.add(new ConcertSession(LocalDateTime.now().minusDays(1), concert));

        concertSessionJpaRepository.saveAll(concertSessionList);

        mockMvc.perform(get("/concert/{concertId}/session", concert.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(listSize))
                .andDo(print())
                .andReturn();

    }

    @Test
    @DisplayName("날짜 반환 - 잘못된 concertId 테스트")
    public void getSessionDateNotFoundConcertIdTest() throws Exception {
//        Concert concert = concertJpaRepository.save(new Concert("test"));
//        long listSize = 5L;
//        List<ConcertSession> sessionList = new ArrayList<>();
//        for(int i = 1; i <= listSize; i++){
//            sessionList.add(new ConcertSession(LocalDateTime.now().plusDays(i), concert));
//        }
//        sessionList.add(new ConcertSession(LocalDateTime.now().minusDays(1), concert));
//
//        concertSessionJpaRepository.saveAll(sessionList);

        mockMvc.perform(get("/concert/{concertId}/session", 0L))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.msg").value(ErrorCode.NOT_FOUND_CONCERT_ID.getMsg()))
                .andDo(print())
                .andReturn();

    }


    @Test
    @DisplayName("좌석 반환 - 성공 테스트")
    public void getSessionSeatTest() throws Exception {
        Concert concert = concertJpaRepository.save(new Concert("test"));
        ConcertSession concertSession = concertSessionJpaRepository.save(new ConcertSession(LocalDateTime.now().plusDays(1), concert));

        List<ConcertSeat> concertSeatList = new ArrayList<>();

        long listSize = 5L;
        for(int i = 1; i <= listSize; i++){
            concertSeatList.add(new ConcertSeat( i, 1000, false, concertSession.getId()));
        }

        concertSeatJpaRepository.saveAll(concertSeatList);

        mockMvc.perform(get("/concert/{concertId}/seat", concert.getId()).param("sessionId", String.valueOf(concertSession.getId())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.seatList.size()").value(listSize))
                .andDo(print())
                .andReturn();

    }

    @Test
    @DisplayName("좌석 반환 - 잘못된 concertId 테스트")
    public void getSessionSeatInvalidConcertIdTest() throws Exception {
        Concert concert = concertJpaRepository.save(new Concert("test"));
        ConcertSession concertSession = concertSessionJpaRepository.save(new ConcertSession(LocalDateTime.now().plusDays(1), concert));

        List<ConcertSeat> concertSeatList = new ArrayList<>();

        long listSize = 5L;
        for(int i = 1; i <= listSize; i++){
            concertSeatList.add(new ConcertSeat( i, 1000, false, concertSession.getId()));
        }

        concertSeatJpaRepository.saveAll(concertSeatList);

        mockMvc.perform(get("/concert/{concertId}/seat", 23L).param("sessionId", String.valueOf(concertSession.getId())))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.msg").value(ErrorCode.NOT_FOUND_CONCERT_ID.getMsg()))
                .andDo(print());

    }

    @Test
    @DisplayName("좌석 반환 - 잘못된 sessionId 테스트")
    public void getSessionSeatInvalidSessionIdTest() throws Exception {
        Concert concert = concertJpaRepository.save(new Concert("test"));
        ConcertSession concertSession = concertSessionJpaRepository.save(new ConcertSession(LocalDateTime.now().plusDays(1), concert));

        List<ConcertSeat> concertSeatList = new ArrayList<>();

        long listSize = 5L;
        for(int i = 1; i <= listSize; i++){
            concertSeatList.add(new ConcertSeat( i, 1000, false, concertSession.getId()));
        }

        concertSeatJpaRepository.saveAll(concertSeatList);

        mockMvc.perform(get("/concert/{concertId}/seat", concert.getId()).param("sessionId", String.valueOf(0)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.msg").value(ErrorCode.NOT_FOUND_SESSION_ID.getMsg()))
                .andDo(print());

    }

    @Test
    @DisplayName("예약 - 성공 테스트")
    public void reservationTest() throws Exception {
        User user = userJpaRepository.save(new User("", 12312030));
        Concert concert = concertJpaRepository.save(new Concert("test"));
        ConcertSession concertSession = concertSessionJpaRepository.save(new ConcertSession(LocalDateTime.now().plusDays(1), concert));

        List<ConcertSeat> concertSeatList = new ArrayList<>();

        long listSize = 5L;
        int i = 1;
        for(; i <= listSize; i++){
            concertSeatList.add(new ConcertSeat( i, 1000, false, concertSession.getId()));
        }
        int seatNumber = i;
        ConcertSeat concertSeat = new ConcertSeat(seatNumber, 1000, true, concertSession.getId());
        concertSeatList.add(concertSeat);


        concertSeatJpaRepository.saveAll(concertSeatList);
        ReservationRequestDto requestDto = new ReservationRequestDto(concert.getId(), concertSession.getId(), concertSeat.getId(), user.getId());
        MvcResult mvcResult = mockMvc.perform(post("/concert/reservation")
                        .header(JwtUtil.AUTHORIZATION_HEADER, "Bearer " + validToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isOk())
//                .andDo(print())
                .andReturn();

        String jsonResponse1 = mvcResult.getResponse().getContentAsString();
        ReservationResponseDto responseDto = objectMapper.readValue(jsonResponse1, ReservationResponseDto.class);

        Optional<Reservation> reservation = reservationJpaRepository.findByIdAndUserId(responseDto.getReservationId(), user.getId());

        assertTrue(reservation.isPresent());
        assertEquals(reservation.get().getId(), responseDto.getReservationId());
        assertEquals(concertSeat.getPrice(), responseDto.getPrice());

    }

    @Test
    @DisplayName("예약 - 잘못된 userId 테스트")
    public void reservationNotFoundUserTest() throws Exception {
        User user = userJpaRepository.save(new User("", 12312030));
        Concert concert = concertJpaRepository.save(new Concert("test"));
        ConcertSession concertSession = concertSessionJpaRepository.save(new ConcertSession(LocalDateTime.now().plusDays(1), concert));

        List<ConcertSeat> concertSeatList = new ArrayList<>();

        long listSize = 5L;
        int i = 1;
        for(; i <= listSize; i++){
            concertSeatList.add(new ConcertSeat(i, 1000, false, concertSession.getId()));
        }
        ConcertSeat concertSeat = new ConcertSeat(i+1, 1000, true, concertSession.getId());
        concertSeatList.add(concertSeat);


        concertSeatJpaRepository.saveAll(concertSeatList);
        ReservationRequestDto requestDto = new ReservationRequestDto(concert.getId(), concertSession.getId(), concertSeat.getId(), 0);
        mockMvc.perform(post("/concert/reservation")
                        .header(JwtUtil.AUTHORIZATION_HEADER, "Bearer " + validToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.msg").value(ErrorCode.NOT_FOUND_USER_ID.getMsg()))
//                .andDo(print())
                .andReturn();
    }

    @Test
    @DisplayName("예약 - 잘못된 concertId 테스트")
    public void reservationNotFoundConcertIdTest() throws Exception {
        User user = userJpaRepository.save(new User("", 12312030));
        Concert concert = concertJpaRepository.save(new Concert("test"));
        ConcertSession concertSession = concertSessionJpaRepository.save(new ConcertSession(LocalDateTime.now().plusDays(1), concert));

        List<ConcertSeat> concertSeatList = new ArrayList<>();

        long listSize = 5L;
        int i = 1;
        for(; i <= listSize; i++){
            concertSeatList.add(new ConcertSeat( i, 1000, false, concertSession.getId()));
        }
        int seatNumber = i;
        ConcertSeat concertSeat = new ConcertSeat(seatNumber, 1000, true, concertSession.getId());
        concertSeatList.add(concertSeat);


        concertSeatJpaRepository.saveAll(concertSeatList);
        ReservationRequestDto requestDto = new ReservationRequestDto(concert.getId()+1, concertSession.getId(), concertSeat.getId(), user.getId());
        mockMvc.perform(post("/concert/reservation")
                        .header(JwtUtil.AUTHORIZATION_HEADER, "Bearer " + validToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.msg").value(ErrorCode.NOT_FOUND_CONCERT_ID.getMsg()))
//                .andDo(print())
                .andReturn();
    }

    @Test
    @DisplayName("예약 - 잘못된 sessionId 테스트")
    public void reservationNotFoundSessionIdTest() throws Exception {
        User user = userJpaRepository.save(new User("", 12312030));
        Concert concert = concertJpaRepository.save(new Concert("test"));
        ConcertSession concertSession = concertSessionJpaRepository.save(new ConcertSession(LocalDateTime.now().plusDays(1), concert));

        List<ConcertSeat> concertSeatList = new ArrayList<>();

        long listSize = 5L;
        int i = 1;
        for(; i <= listSize; i++){
            concertSeatList.add(new ConcertSeat( i, 1000, false, concertSession.getId()));
        }
        int seatNumber = i;
        ConcertSeat concertSeat = new ConcertSeat(seatNumber, 1000, true, concertSession.getId());
        concertSeatList.add(concertSeat);


        concertSeatJpaRepository.saveAll(concertSeatList);
        ReservationRequestDto requestDto = new ReservationRequestDto(concert.getId(), concertSession.getId()+1, concertSeat.getId(), user.getId());
        mockMvc.perform(post("/concert/reservation")
                        .header(JwtUtil.AUTHORIZATION_HEADER, "Bearer " + validToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.msg").value(ErrorCode.NOT_FOUND_SESSION_ID.getMsg()))
                .andDo(print())
                .andReturn();
    }

    @Test
    @DisplayName("예약 - 잘못된 seatNumber 테스트")
    public void reservationNotFoundSeatNumberTest() throws Exception {
        User user = userJpaRepository.save(new User("", 12312030));
        Concert concert = concertJpaRepository.save(new Concert("test"));
        ConcertSession concertSession = concertSessionJpaRepository.save(new ConcertSession(LocalDateTime.now().plusDays(1), concert));

        List<ConcertSeat> concertSeatList = new ArrayList<>();

        long listSize = 5L;
        int i = 1;
        for(; i <= listSize; i++){
            concertSeatList.add(new ConcertSeat(i, 1000, false, concertSession.getId()));
        }
        int seatNumber = i;
        ConcertSeat concertSeat = new ConcertSeat(seatNumber, 1000, true, concertSession.getId());
        concertSeatList.add(concertSeat);


        concertSeatJpaRepository.saveAll(concertSeatList);
        ReservationRequestDto requestDto = new ReservationRequestDto(concert.getId(), concertSession.getId(), 0, user.getId());
        mockMvc.perform(post("/concert/reservation")
                        .header(JwtUtil.AUTHORIZATION_HEADER, "Bearer " + validToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.msg").value(ErrorCode.NOT_FOUND_SEAT_ID.getMsg()))
                .andDo(print())
                .andReturn();
    }




}
