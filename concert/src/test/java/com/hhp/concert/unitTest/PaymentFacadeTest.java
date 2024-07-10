package com.hhp.concert.unitTest;

import com.hhp.concert.Business.Domain.User;
import com.hhp.concert.Business.dto.ChargeResponseDto;
import com.hhp.concert.Business.service.UserService;
import com.hhp.concert.application.PaymentFacade;
import com.hhp.concert.util.CustomException;
import com.hhp.concert.util.ErrorCode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

public class PaymentFacadeTest {

    @Mock
    private UserService userService;

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

        when(userService.chargePoint(userId, amount)).thenReturn(user);

        ChargeResponseDto response = paymentFacade.charge(userId, amount);

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
}