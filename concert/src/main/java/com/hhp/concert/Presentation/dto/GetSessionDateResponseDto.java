package com.hhp.concert.Presentation.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class GetSessionDateResponseDto {
    private long sessionId;
    private LocalDate date;
    private int available;
}
