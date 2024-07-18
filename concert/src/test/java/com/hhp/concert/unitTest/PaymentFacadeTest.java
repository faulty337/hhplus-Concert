package com.hhp.concert.unitTest;

import com.hhp.concert.Business.Domain.*;
import com.hhp.concert.Business.Domain.enums.ReservationStatus;
import com.hhp.concert.Business.dto.PaymentResponseDto;
import com.hhp.concert.Business.dto.UserBalanceResponseDto;
import com.hhp.concert.Business.service.*;
import com.hhp.concert.application.PaymentFacade;
import com.hhp.concert.util.CustomException;
import com.hhp.concert.util.ErrorCode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDateTime;
import java.util.Optional;

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
    private QueueService queueService;

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
        int price = 5000;
        String token = "valid-token";
        Concert concert = new Concert(concertId, "test");
        User user = new User(userId, "token", 10000);

        Session session = new Session(sessionId, LocalDateTime.now().plusDays(1), concert);

        Seat seat = new Seat(1, price, false, session);

        Reservation reservation = new Reservation(user, session, seat, price);

        PaymentHistory paymentHistory = new PaymentHistory(price, user, reservation);

        given(queueService.isProcessing(userId)).willReturn(true);
        given(userService.getUser(userId)).willReturn(Optional.of(user));
        given(jwtService.isProcessing(token, userId)).willReturn(true);
        given(reservationService.getReservationByUserId(userId, reservationId)).willReturn(reservation);
        given(paymentService.addPaymentHistory(any(PaymentHistory.class))).willReturn(paymentHistory);

        PaymentResponseDto response = paymentFacade.payment(userId, reservationId);

        assertNotNull(response);
        assertEquals(session.getId(), response.getSessionId());
        assertEquals(session.getSessionTime(), response.getDate());
        assertEquals(seat.getSeatNumber(), response.getSeatNumber());
        assertEquals(paymentHistory.getAmount(), response.getPrice());

        verify(userService, times(1)).getUser(userId);
        verify(jwtService, times(1)).isProcessing(token, userId);
        verify(reservationService, times(1)).getReservationByUserId(userId, reservationId);
        verify(paymentService, times(1)).addPaymentHistory(any(PaymentHistory.class));
    }

    @Test
    public void testPaymentUserNotFound() {
        long userId = 1L;
        long reservationId = 1L;
        String token = "token";

        given(userService.getUser(userId)).willReturn(Optional.empty());

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
    public void testPaymentNotFoundProcessQueue() {
        long userId = 1L;
        long reservationId = 1L;
        String token = "token";

        User user = new User(userId, "token", 1000);
        given(userService.getUser(userId)).willReturn(Optional.of(user));
        given(jwtService.isProcessing(token, userId)).willReturn(true);
        given(queueService.isProcessing(userId)).willReturn(false);


        CustomException exception = assertThrows(CustomException.class, () -> {
            paymentFacade.payment(userId, reservationId);
        });

        assertEquals(ErrorCode.IS_NOT_PROCESSING.getMsg(), exception.getMsg());

        verify(userService, times(1)).getUser(userId);
    }

    @Test
    public void testPaymentInvalidToken() {
        String token = "invalid-token";
        long userId = 1L;
        long reservationId = 1L;
        long concertId = 1L;
        int price = 5000;
        Concert concert = new Concert(concertId, "test");
        User user = new User(userId, "token", 1000);

        Session session = new Session(LocalDateTime.now().plusDays(1), concert);


        given(userService.getUser(userId)).willReturn(Optional.of(user));
        given(jwtService.isProcessing(token, userId)).willReturn(false);

        CustomException exception = assertThrows(CustomException.class, () -> {
            paymentFacade.payment(userId, reservationId);
        });

        assertEquals(ErrorCode.INVALID_TOKEN_STATE.getMsg(), exception.getMsg());

        verify(userService, times(1)).getUser(userId);
        verify(jwtService, times(1)).isProcessing(token, userId);
        verify(reservationService, never()).getReservationByUserId(anyLong(), anyLong());
        verify(paymentService, never()).addPaymentHistory(any(PaymentHistory.class));
    }

    @Test
    public void testPaymentInsufficientFunds() {
        long userId = 1L;
        long reservationId = 1L;
        long concertId = 1L;
        int price = 5000;
        String token = "valid-token";
        Concert concert = new Concert(concertId, "test");
        User user = new User(userId, "token", 1000);

        Session session = new Session(LocalDateTime.now().plusDays(1), concert);

        Seat seat = new Seat(1, price, false, session);

        Reservation reservation = new Reservation(reservationId, user, session, seat, price, ReservationStatus.PENDING);

        given(userService.getUser(userId)).willReturn(Optional.of(user));
        given(jwtService.isProcessing(token, userId)).willReturn(true);
        given(queueService.isProcessing(userId)).willReturn(true);
        given(reservationService.getReservationByUserId(userId, reservationId)).willReturn(reservation);

        CustomException exception = assertThrows(CustomException.class, () -> {
            paymentFacade.payment(userId, reservationId);
        });

        assertEquals(ErrorCode.INSUFFICIENT_FUNDS.getMsg(), exception.getMsg());

        verify(userService, times(1)).getUser(userId);
        verify(jwtService, times(1)).isProcessing(token, userId);
        verify(reservationService, times(1)).getReservationByUserId(userId, reservationId);
        verify(paymentService, never()).addPaymentHistory(any(PaymentHistory.class));
    }

}