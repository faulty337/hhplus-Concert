package com.hhp.concert.unitTest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hhp.concert.Business.Domain.*;
import com.hhp.concert.Business.Domain.enums.ReservationStatus;
import com.hhp.concert.Business.dto.ReservationRequestDto;
import com.hhp.concert.Business.service.*;
import com.hhp.concert.util.JwtUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class JwtFilterTest {


    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private JwtUtil jwtUtil;

    @MockBean
    private UserService userService;

    @MockBean
    private ConcertService concertService;

    @MockBean
    private SessionService sessionService;

    @MockBean
    private SeatService seatService;

    @MockBean
    private ReservationService reservationService;

    @Autowired
    private ObjectMapper objectMapper;

    private String validToken;
    private String invalidToken;

    @BeforeEach
    public void setUp() {
        validToken = "valid-jwt-token";
        invalidToken = "invalid-jwt-token";

        when(jwtUtil.validateToken(validToken)).thenReturn(true);
        when(jwtUtil.validateToken(invalidToken)).thenReturn(false);
        when(jwtUtil.extractSign(validToken)).thenReturn("1");

    }

    @Test
    public void testValidToken() throws Exception {
        long userId = 1231L;
        Long concertId = 235L;
        Long sessionId = 2141L;
        Long seatId = 2L;
        Long reservationId = 23L;
        String requestBody = objectMapper.writeValueAsString(new ReservationRequestDto(sessionId, seatId, userId));
        User user = new User(userId, null, 1000);
        Concert concert = new Concert(concertId, "test");
        Session session = new Session(sessionId, LocalDateTime.now(), concert);
        Seat seat = new Seat(seatId, 1, 1000, false, session);
        Reservation reservation = new Reservation(reservationId, user, session, seat, seat.getPrice(), ReservationStatus.PENDING);

        when(concertService.getConcert(concertId)).thenReturn(concert);
        when(sessionService.getSessionByOpenAndConcertId(concertId, sessionId)).thenReturn(session);
        when(seatService.getSeatsForConcertSessionAndAvailable(sessionId, seatId)).thenReturn(seat);
        when(userService.getUser(userId)).thenReturn(Optional.of(user));
        when(reservationService.addReservation(any(Reservation.class))).thenReturn(reservation);

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.post("/concert/{concertId}/reservation", concertId)
                        .header(JwtUtil.AUTHORIZATION_HEADER, "Bearer " + validToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();
    }

    @Test
    public void testInvalidToken() throws Exception {
        String requestBody = objectMapper.writeValueAsString(new ReservationRequestDto(1, 1, 1));

        mockMvc.perform(MockMvcRequestBuilders.post("/concert/{concertId}/reservation", 1)
                        .header(JwtUtil.AUTHORIZATION_HEADER, "Bearer " + invalidToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isUnauthorized());
    }

    @Test
    public void testNoToken() throws Exception {
        String requestBody = objectMapper.writeValueAsString(new ReservationRequestDto(1, 1, 1));

        mockMvc.perform(MockMvcRequestBuilders.post("/concert/{concertId}/reservation", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isUnauthorized());
    }

    @Test
    public void testNonFilteredUrl() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/concert/waiting/status"))
                .andDo(print())
                .andExpect(status().isOk());
    }


}
