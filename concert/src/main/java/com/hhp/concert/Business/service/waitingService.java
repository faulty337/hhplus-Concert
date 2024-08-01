package com.hhp.concert.Business.service;

import com.hhp.concert.Business.Domain.ProcessQueue;
import com.hhp.concert.Business.Domain.WaitingQueue;

import java.util.Optional;

public interface waitingService {
    public Optional<WaitingQueue> waitingQueueByUserId(Long userId);

    public Long getWaitingNumber(Long userId);

    public WaitingQueue addWaiting(WaitingQueue waitingQueue);

    boolean isProcessing(long userId);

    void removeProcessing(ProcessQueue processQueue);

    void updateQueue();

    void moveUserToProcessingQueue();
    void moveToProcessingQueue();

    ProcessQueue addProcessingQueue(Long userId);
    void moveUserToProcessingQueue(Long userId);

    void removeProcessingByUserId(Long userId);
}
