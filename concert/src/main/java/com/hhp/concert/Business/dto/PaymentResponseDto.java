package com.hhp.concert.Business.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@NoArgsConstructor
@Getter
@AllArgsConstructor
public class PaymentResponseDto {
    private long sessionId;
    private LocalDateTime date;
    private int seatNumber;
    private int price;
}
