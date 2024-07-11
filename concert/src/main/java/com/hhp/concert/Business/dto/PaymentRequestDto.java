package com.hhp.concert.Business.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class PaymentRequestDto {
    private long userId;
    private long reservationId;
    private String token;
}
