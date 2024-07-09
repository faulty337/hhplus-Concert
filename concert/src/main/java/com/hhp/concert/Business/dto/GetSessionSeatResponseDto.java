package com.hhp.concert.Business.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class GetSessionSeatResponseDto {
    private LocalDateTime date;
    private List<SeatInfoDto> seatList;
}
