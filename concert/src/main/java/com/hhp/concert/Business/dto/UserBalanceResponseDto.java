package com.hhp.concert.Business.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Getter
public class UserBalanceResponseDto {
    private long userId;
    private int balance;
}
