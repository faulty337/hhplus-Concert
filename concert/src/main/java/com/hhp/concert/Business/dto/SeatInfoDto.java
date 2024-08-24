package com.hhp.concert.Business.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class SeatInfoDto {
    private long seatId;
    private int seatNumber;
    private boolean isAvailable;
    private int price;
}
