package com.hhp.concert.Presentation.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class GetSessionSeatResponseDto {
    private LocalDate date;
    private List<SeatInfoDto> seatList;
}
