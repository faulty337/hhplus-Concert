package com.hhp.concert.unitTest;

import com.hhp.concert.Business.Domain.*;
import com.hhp.concert.Business.dto.PaymentResponseDto;
import com.hhp.concert.Business.dto.UserBalanceResponseDto;
import com.hhp.concert.Business.service.*;
import com.hhp.concert.application.PaymentFacade;
import com.hhp.concert.util.exception.CustomException;
import com.hhp.concert.util.exception.ErrorCode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

public class PaymentFacadeTest {

    @Mock
    private UserService userService;

    @Mock
    private JwtService jwtService;

    @Mock
    private ReservationService reservationService;

    @Mock
    private PaymentService paymentService;

    @Mock
    private ConcertService concertService;

    @Mock
    private WaitingService waitingService;

    @InjectMocks
    private PaymentFacade paymentFacade;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testCharge() {
        long userId = 1L;
        int balance = 1000;
        int amount = 10000;
        User user = new User(userId, "token", balance + amount);

        given(userService.chargePoint(userId, amount)).willReturn(user);

        UserBalanceResponseDto response = paymentFacade.charge(userId, amount);

        assertEquals(1L, response.getUserId());
        assertEquals(balance + amount, response.getBalance());
        verify(userService, times(1)).chargePoint(userId, amount);
    }

    @Test
    void testChargeInvalidAmount() {
        doThrow(new CustomException(ErrorCode.INVALID_AMOUNT)).when(userService).chargePoint(1L, 0);

        CustomException exception = assertThrows(CustomException.class, () -> {
            paymentFacade.charge(1L, 0);
        });

        assertEquals(ErrorCode.INVALID_AMOUNT.getMsg(), exception.getMsg());
        verify(userService, times(1)).chargePoint(1L, 0);
    }

    @Test
    void testChargeUserNotFound() {
        doThrow(new CustomException(ErrorCode.NOT_FOUND_USER_ID)).when(userService).chargePoint(1L, 100);

        CustomException exception = assertThrows(CustomException.class, () -> {
            paymentFacade.charge(1L, 100);
        });

        assertEquals(ErrorCode.NOT_FOUND_USER_ID.getMsg(), exception.getMsg());
        verify(userService, times(1)).chargePoint(1L, 100);
    }

    @Test
    public void testPayment_Success() {
        long userId = 1L;
        long reservationId = 1L;
        long concertId = 1L;
        long sessionId = 1L;
        long seatId = 1L;
        int price = 5000;
        String token = "valid-token";
        Concert concert = new Concert(concertId, "test");
        User user = new User(userId, "token", 10000);

        ConcertSession concertSession = new ConcertSession(sessionId, LocalDateTime.now().plusDays(1), concertId);

        ConcertSeat concertSeat = new ConcertSeat(seatId, 1, price, false, concertSession.getId());

        Reservation reservation = new Reservation(user.getId(), concert.getId(), concertSession.getId(), concertSeat.getId(), price);

        PaymentHistory paymentHistory = new PaymentHistory(price, user, reservation);

        given(userService.getUser(userId)).willReturn(user);
        given(reservationService.getReservationByUserId(userId, reservationId)).willReturn(reservation);
        given(paymentService.addPaymentHistory(any(PaymentHistory.class))).willReturn(paymentHistory);
        given(concertService.getConcertBySessionId(sessionId)).willReturn(concert);
        given(concertService.getSeat(concertId, sessionId, concertSeat.getId())).willReturn(concertSeat);
        given(concertService.getSession(sessionId)).willReturn(concertSession);

        PaymentResponseDto response = paymentFacade.payment(userId, reservationId);

        assertNotNull(response);
        assertEquals(concertSession.getId(), response.getSessionId());
        assertEquals(concertSession.getSessionTime(), response.getDate());
        assertEquals(concertSeat.getSeatNumber(), response.getSeatNumber());
        assertEquals(paymentHistory.getAmount(), response.getPrice());

        verify(userService, times(1)).getUser(userId);
        verify(reservationService, times(1)).getReservationByUserId(userId, reservationId);
        verify(paymentService, times(1)).addPaymentHistory(any(PaymentHistory.class));
    }

    @Test
    public void testPaymentUserNotFound() {
        long userId = 1L;
        long reservationId = 1L;
        String token = "token";

        given(userService.getUser(userId)).willThrow(new CustomException(ErrorCode.NOT_FOUND_USER_ID));

        CustomException exception = assertThrows(CustomException.class, () -> {
            paymentFacade.payment(userId, reservationId);
        });

        assertEquals(ErrorCode.NOT_FOUND_USER_ID.getMsg(), exception.getMsg());

        verify(userService, times(1)).getUser(userId);
        verify(jwtService, never()).isProcessing(token, userId);
        verify(reservationService, never()).getReservationByUserId(anyLong(), anyLong());
        verify(paymentService, never()).addPaymentHistory(any(PaymentHistory.class));
    }

    @Test
    public void testPaymentInsufficientFunds() {
        long userId = 1L;
        long reservationId = 1L;
        long concertId = 1L;
        long sessionId = 123L;
        int price = 5000;
        String token = "valid-token";
        Concert concert = new Concert(concertId, "test");
        User user = new User(userId, "token", 1000);

        ConcertSession concertSession = new ConcertSession(sessionId, LocalDateTime.now().plusDays(1), concertId);

        ConcertSeat concertSeat = new ConcertSeat(1, price, false, 1L);

        Reservation reservation = new Reservation(reservationId, concert.getId(), user.getId(), concertSession.getId(), concertSeat.getId(), price, Reservation.ReservationStatus.PENDING);
        PaymentHistory paymentHistory = new PaymentHistory(concertSeat.getPrice(), user, reservation);

        given(userService.getUser(userId)).willReturn(user);
        given(reservationService.getReservationByUserId(userId, reservationId)).willReturn(reservation);
        given(concertService.getConcertBySessionId(concertSession.getId())).willReturn(concert);
        given(concertService.getSeat(concertId, concertSession.getId(), concertSeat.getId())).willReturn(concertSeat);
        given(concertService.getSession(concertSession.getId())).willReturn(concertSession);
        given(paymentService.addPaymentHistory(any(PaymentHistory.class))).willReturn(paymentHistory);
        given(userService.usePoint(user.getId(), concertSeat.getPrice())).willThrow(new CustomException(ErrorCode.INSUFFICIENT_FUNDS));

        CustomException exception = assertThrows(CustomException.class, () -> {
            paymentFacade.payment(userId, reservationId);
        });

        assertEquals(ErrorCode.INSUFFICIENT_FUNDS.getMsg(), exception.getMsg());

        verify(userService, times(1)).getUser(userId);
        verify(reservationService, times(1)).getReservationByUserId(userId, reservationId);
        verify(paymentService, never()).addPaymentHistory(any(PaymentHistory.class));
    }

}