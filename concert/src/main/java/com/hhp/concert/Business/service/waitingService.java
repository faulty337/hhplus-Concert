package com.hhp.concert.Business.service;

import com.hhp.concert.Business.Domain.ProcessQueue;
import com.hhp.concert.Business.Domain.WaitingQueue;

import java.util.Optional;

public interface waitingService {
    public Long getWaitingNumber(Long userId);
    void updateQueue();

    void moveToProcessingQueue();

    void moveUserToProcessingQueue(Long userId);
}
