package com.hhp.concert.application;

import com.hhp.concert.Business.Domain.User;
import com.hhp.concert.Business.dto.UserBalanceResponseDto;
import com.hhp.concert.Business.service.UserService;
import com.hhp.concert.util.CustomException;
import com.hhp.concert.util.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PaymentFacade {
    private final UserService userService;


    public UserBalanceResponseDto charge(Long userId, int amount) {
        User user = userService.chargePoint(userId, amount);

        return new UserBalanceResponseDto(user.getId(), user.getBalance());
    }

    public UserBalanceResponseDto getBalance(Long userId) {
        User user = userService.getUser(userId).orElseThrow(
                () -> new CustomException(ErrorCode.NOT_FOUND_USER_ID)
        );

        return new UserBalanceResponseDto(user.getId(), user.getBalance());


    }
}
