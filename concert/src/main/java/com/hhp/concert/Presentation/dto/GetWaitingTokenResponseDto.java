package com.hhp.concert.Presentation.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class GetWaitingTokenResponseDto {
    private int waitingNumber;
    private String token;
    private boolean isProcessing;
}
