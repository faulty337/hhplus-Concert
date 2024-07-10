package com.hhp.concert.Business.service;

import com.hhp.concert.Business.Domain.WaitingQueue;

import java.util.Optional;

public interface QueueService {
    public Optional<WaitingQueue> waitingQueueByUserId(Long userId);

    public Long getWaitingNumber(Long userId);

    public WaitingQueue addWaiting(WaitingQueue waitingQueue);
}
