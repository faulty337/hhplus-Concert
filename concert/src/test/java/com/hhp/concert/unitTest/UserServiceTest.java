package com.hhp.concert.unitTest;

import com.hhp.concert.Business.Domain.User;
import com.hhp.concert.Business.Repository.UserRepository;
import com.hhp.concert.Business.service.UserServiceImpl;
import com.hhp.concert.util.exception.CustomException;
import com.hhp.concert.util.exception.ErrorCode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.mockito.BDDMockito.*;
import static org.junit.jupiter.api.Assertions.*;

public class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserServiceImpl userService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetUser() {
        long userId = 1L;
        User user = new User(userId, "token", 1000);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        User foundUser = userService.getUser(1L);


        assertEquals(1L, foundUser.getId());
    }

    @Test
    void testUpdateToken() {
        long userId = 1L;
        User user = new User(userId, "token", 1000);
        given(userRepository.findById(userId)).willReturn(Optional.of(user));
        given(userRepository.save(any(User.class))).willReturn(user);

        User updatedUser = userService.updateToken(1L, "newToken");

        assertEquals("newToken", updatedUser.getToken());
        verify(userRepository, times(1)).findById(1L);
        verify(userRepository, times(1)).save(user);
    }

    @Test
    void testUpdateTokenUserNotFound() {
        long userId = 1L;
        given(userRepository.findById(userId)).willReturn(Optional.empty());

        CustomException exception = assertThrows(CustomException.class, () -> {
            userService.updateToken(userId, "newToken");
        });

        assertEquals(ErrorCode.NOT_FOUND_USER_ID.getMsg(), exception.getMsg());
        verify(userRepository, times(1)).findById(1L);
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void testChargePoint() {
        long userId = 1L;
        int balance = 1000;
        int amount = 10000;
        User user = new User(userId, "token", balance);

        given(userRepository.findById(userId)).willReturn(Optional.of(user));
        given(userRepository.save(any(User.class))).willReturn(user);

        User updatedUser = userService.chargePoint(userId, amount);

        assertEquals(balance + amount, updatedUser.getBalance());
        verify(userRepository, times(1)).findById(userId);
        verify(userRepository, times(1)).save(user);
    }

    @Test
    void testChargePointInvalidAmount() {
        long userId = 1L;
        User user = new User(userId, "token", 1000);
        given(userRepository.findById(userId)).willReturn(Optional.of(user));

        CustomException exception = assertThrows(CustomException.class, () -> {
            userService.chargePoint(userId, 0);
        });

        assertEquals(ErrorCode.INVALID_AMOUNT.getMsg(), exception.getMsg());
        verify(userRepository, times(1)).findById(userId);
        verify(userRepository, never()).save(user);
    }

    @Test
    void testChargePointUserNotFound() {
        Long userId = 1L;
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        CustomException exception = assertThrows(CustomException.class, () -> {
            userService.chargePoint(userId, 100);
        });

        assertEquals(ErrorCode.NOT_FOUND_USER_ID.getMsg(), exception.getMsg());
        verify(userRepository, times(1)).findById(1L);
        verify(userRepository, never()).save(any(User.class));
    }
}