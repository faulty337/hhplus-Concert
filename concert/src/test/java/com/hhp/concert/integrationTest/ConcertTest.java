package com.hhp.concert.integrationTest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hhp.concert.Business.Domain.*;
import com.hhp.concert.Business.dto.ReservationRequestDto;
import com.hhp.concert.Business.dto.ReservationResponseDto;
import com.hhp.concert.Infrastructure.concert.ConcertJpaRepository;
import com.hhp.concert.Infrastructure.reservation.ReservationJpaRepository;
import com.hhp.concert.Infrastructure.seat.SeatJpaRepository;
import com.hhp.concert.Infrastructure.session.SessionJpaRepository;
import com.hhp.concert.Infrastructure.user.UserJpaRepository;
import com.hhp.concert.util.exception.ErrorCode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.verify;
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
    private SessionJpaRepository sessionJpaRepository;
    @Autowired
    private SeatJpaRepository seatJpaRepository;
    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private UserJpaRepository userJpaRepository;
    @Autowired
    private ReservationJpaRepository reservationJpaRepository;

    @BeforeEach
    public void setUp(){
        reservationJpaRepository.deleteAll();
        seatJpaRepository.deleteAll();
        sessionJpaRepository.deleteAll();
        concertJpaRepository.deleteAll();

    }

    @Test
    @DisplayName("날짜 반환 - 성공 테스트")
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
    @DisplayName("날짜 반환 - 잘못된 concertId 테스트")
    public void getSessionDateNotFoundConcertIdTest() throws Exception {
//        Concert concert = concertJpaRepository.save(new Concert("test"));
//        long listSize = 5L;
//        List<Session> sessionList = new ArrayList<>();
//        for(int i = 1; i <= listSize; i++){
//            sessionList.add(new Session(LocalDateTime.now().plusDays(i), concert));
//        }
//        sessionList.add(new Session(LocalDateTime.now().minusDays(1), concert));
//
//        sessionJpaRepository.saveAll(sessionList);

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
        Session concertSession = sessionJpaRepository.save(new Session(LocalDateTime.now().plusDays(1), concert));

        List<Seat> seatList = new ArrayList<>();

        long listSize = 5L;
        for(int i = 1; i <= listSize; i++){
            seatList.add(new Seat( i, 1000, false, concertSession));
        }

        seatJpaRepository.saveAll(seatList);

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
        Session concertSession = sessionJpaRepository.save(new Session(LocalDateTime.now().plusDays(1), concert));

        List<Seat> seatList = new ArrayList<>();

        long listSize = 5L;
        for(int i = 1; i <= listSize; i++){
            seatList.add(new Seat( i, 1000, false, concertSession));
        }

        seatJpaRepository.saveAll(seatList);

        mockMvc.perform(get("/concert/{concertId}/seat", 23L).param("sessionId", String.valueOf(concertSession.getId())))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.msg").value(ErrorCode.NOT_FOUND_CONCERT_ID.getMsg()))
                .andDo(print());

    }

    @Test
    @DisplayName("좌석 반환 - 잘못된 sessionId 테스트")
    public void getSessionSeatInvalidSessionIdTest() throws Exception {
        Concert concert = concertJpaRepository.save(new Concert("test"));
        Session concertSession = sessionJpaRepository.save(new Session(LocalDateTime.now().plusDays(1), concert));

        List<Seat> seatList = new ArrayList<>();

        long listSize = 5L;
        for(int i = 1; i <= listSize; i++){
            seatList.add(new Seat( i, 1000, false, concertSession));
        }

        seatJpaRepository.saveAll(seatList);

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
        Session session = sessionJpaRepository.save(new Session(LocalDateTime.now().plusDays(1), concert));

        List<Seat> seatList = new ArrayList<>();

        long listSize = 5L;
        int i = 1;
        for(; i <= listSize; i++){
            seatList.add(new Seat( i, 1000, false, session));
        }
        int seatNumber = i;
        Seat seat = new Seat(seatNumber, 1000, true, session);
        seatList.add(seat);


        seatJpaRepository.saveAll(seatList);
        ReservationRequestDto requestDto = new ReservationRequestDto(concert.getId(), session.getId(), seat.getId(), user.getId());
        MvcResult mvcResult = mockMvc.perform(post("/concert/reservation")
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
        assertEquals(seat.getPrice(), responseDto.getPrice());

    }

    @Test
    @DisplayName("예약 - 잘못된 userId 테스트")
    public void reservationNotFoundUserTest() throws Exception {
        User user = userJpaRepository.save(new User("", 12312030));
        Concert concert = concertJpaRepository.save(new Concert("test"));
        Session session = sessionJpaRepository.save(new Session(LocalDateTime.now().plusDays(1), concert));

        List<Seat> seatList = new ArrayList<>();

        long listSize = 5L;
        int i = 1;
        for(; i <= listSize; i++){
            seatList.add(new Seat(i, 1000, false, session));
        }
        Seat seat = new Seat(i+1, 1000, true, session);
        seatList.add(seat);


        seatJpaRepository.saveAll(seatList);
        ReservationRequestDto requestDto = new ReservationRequestDto(concert.getId(), session.getId(), seat.getId(), 0);
        mockMvc.perform(post("/concert/reservation")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.msg").value(ErrorCode.NOT_FOUND_USER_ID.getMsg()))
//                .andDo(print())
                .andReturn();
    }

    @Test
    @DisplayName("예약 - 잘못된 userId 테스트")
    public void reservationNotFoundConcertIdTest() throws Exception {
        User user = userJpaRepository.save(new User("", 12312030));
        Concert concert = concertJpaRepository.save(new Concert("test"));
        Session session = sessionJpaRepository.save(new Session(LocalDateTime.now().plusDays(1), concert));

        List<Seat> seatList = new ArrayList<>();

        long listSize = 5L;
        int i = 1;
        for(; i <= listSize; i++){
            seatList.add(new Seat( i, 1000, false, session));
        }
        int seatNumber = i;
        Seat seat = new Seat(seatNumber, 1000, true, session);
        seatList.add(seat);


        seatJpaRepository.saveAll(seatList);
        ReservationRequestDto requestDto = new ReservationRequestDto(concert.getId()+1, session.getId(), seat.getId(), user.getId());
        mockMvc.perform(post("/concert/reservation")
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
        Session session = sessionJpaRepository.save(new Session(LocalDateTime.now().plusDays(1), concert));

        List<Seat> seatList = new ArrayList<>();

        long listSize = 5L;
        int i = 1;
        for(; i <= listSize; i++){
            seatList.add(new Seat( i, 1000, false, session));
        }
        int seatNumber = i;
        Seat seat = new Seat(seatNumber, 1000, true, session);
        seatList.add(seat);


        seatJpaRepository.saveAll(seatList);
        ReservationRequestDto requestDto = new ReservationRequestDto(concert.getId(), session.getId()+1, seat.getId(), user.getId());
        mockMvc.perform(post("/concert/reservation")
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
        Session session = sessionJpaRepository.save(new Session(LocalDateTime.now().plusDays(1), concert));

        List<Seat> seatList = new ArrayList<>();

        long listSize = 5L;
        int i = 1;
        for(; i <= listSize; i++){
            seatList.add(new Seat( i, 1000, false, session));
        }
        int seatNumber = i;
        Seat seat = new Seat(seatNumber, 1000, true, session);
        seatList.add(seat);


        seatJpaRepository.saveAll(seatList);
        ReservationRequestDto requestDto = new ReservationRequestDto(concert.getId(), session.getId(), i+1, user.getId());
        mockMvc.perform(post("/concert/reservation")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.msg").value(ErrorCode.NOT_AVAILABLE_SEAT.getMsg()))
                .andDo(print())
                .andReturn();
    }




}
