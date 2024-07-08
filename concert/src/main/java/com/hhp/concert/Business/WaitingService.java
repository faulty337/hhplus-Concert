package com.hhp.concert.Business;

import com.hhp.concert.Business.Domain.WaitingQueue;
import com.hhp.concert.Business.dto.GetWaitingTokenResponseDto;

import java.util.Optional;

public interface WaitingService {
    public Optional<WaitingQueue> findByUserId(Long userId);

    public Long getWaitingNumber(Long userId);

    public WaitingQueue add(WaitingQueue waitingQueue);
}
