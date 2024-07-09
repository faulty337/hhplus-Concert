package com.hhp.concert.Business.service;

import com.hhp.concert.Business.Domain.WaitingQueue;

import java.util.Optional;

public interface WaitingService {
    public Optional<WaitingQueue> findByUserId(Long userId);

    public Long getWaitingNumber(Long userId);

    public WaitingQueue add(WaitingQueue waitingQueue);
}
