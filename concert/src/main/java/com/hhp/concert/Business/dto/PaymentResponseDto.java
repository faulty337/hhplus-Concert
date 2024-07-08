package com.hhp.concert.Business.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@NoArgsConstructor
@Getter
@AllArgsConstructor
public class PaymentResponseDto {
    private long sessionId;
    private LocalDate date;
    private int seatNumber;
}
