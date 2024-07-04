package com.hhp.concert.Presentation.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class ReservationRequestDto {
    private long sessionId;
    private long seatNumber;
}
