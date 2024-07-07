package com.hhp.concert.Business;

import com.hhp.concert.Business.Domain.WaitingQueue;
import com.hhp.concert.Business.dto.GetWaitingTokenResponseDto;

public interface WaitingService {
    public WaitingQueue findByUserId(Long userId);
}
