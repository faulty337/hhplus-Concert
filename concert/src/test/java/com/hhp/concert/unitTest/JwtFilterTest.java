package com.hhp.concert.unitTest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hhp.concert.Business.Domain.*;
import com.hhp.concert.Business.dto.ReservationRequestDto;
import com.hhp.concert.Business.service.*;
import com.hhp.concert.util.JwtUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
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
    private ConcertSessionService concertSessionService;

    @MockBean
    private ConcertSeatService concertSeatService;

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
        Long seatNumber = 2L;
        Long reservationId = 23L;
        String requestBody = objectMapper.writeValueAsString(new ReservationRequestDto(concertId, sessionId, seatNumber, userId));
        User user = new User(userId, null, 1000);
        Concert concert = new Concert(concertId, "test");
        ConcertSession concertSession = new ConcertSession(sessionId, LocalDateTime.now(), concert);
        ConcertSeat concertSeat = new ConcertSeat(seatNumber, 1, 1000, false, concertSession.getId());
        Reservation reservation = new Reservation(reservationId, user.getId(), concertSession.getId(), concertSeat.getId(), concertSeat.getPrice(), Reservation.ReservationStatus.PENDING);

        when(concertService.getConcert(concertId)).thenReturn(concert);
        when(concertSessionService.getSessionByOpenAndConcertId(concertId, sessionId)).thenReturn(concertSession);
        when(concertSeatService.getSeatsForConcertSessionAndAvailable(sessionId, seatNumber)).thenReturn(concertSeat);
        when(userService.getUser(userId)).thenReturn(user);
        when(reservationService.createReservation(any(Reservation.class))).thenReturn(reservation);
        given(concertService.getAvailableReservationSeats(concertId, sessionId, concertSeat.getId())).willReturn(concertSeat);

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.post("/concert/reservation")
                        .header(JwtUtil.AUTHORIZATION_HEADER, "Bearer " + validToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();
    }

    @Test
    public void testInvalidToken() throws Exception {
        String requestBody = objectMapper.writeValueAsString(new ReservationRequestDto(1,1, 1, 1));

        mockMvc.perform(MockMvcRequestBuilders.post("/concert/reservation")
                        .header(JwtUtil.AUTHORIZATION_HEADER, "Bearer " + invalidToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isUnauthorized());
    }

    @Test
    public void testNoToken() throws Exception {
        String requestBody = objectMapper.writeValueAsString(new ReservationRequestDto(1, 1, 1, 1));

        mockMvc.perform(MockMvcRequestBuilders.post("/concert/reservation")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isUnauthorized());
    }

    @Test
    public void testNonFilteredUrl() throws Exception {
        Long userId = 1231L;
        User user = new User(userId, "", 123123);
        when(userService.getUser(userId)).thenReturn(user);
        mockMvc.perform(MockMvcRequestBuilders.get("/concert/balance").param("userId", String.valueOf(userId)))
                .andDo(print())
                .andExpect(status().isOk());
    }


}
