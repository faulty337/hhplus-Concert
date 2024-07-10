package com.hhp.concert.Business.dto;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ChargeRequestDto {
    private Long userId;
    private int amount;
}
