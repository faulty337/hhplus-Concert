package com.hhp.concert.Business.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class GetWaitingTokenResponseDto {
    private long waitingNumber;
    private boolean isProcessing;
}
