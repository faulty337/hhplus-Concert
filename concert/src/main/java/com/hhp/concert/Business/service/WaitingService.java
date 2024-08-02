package com.hhp.concert.Business.service;

public interface WaitingService {
    Long getWaitingNumber(Long userId);

    String enqueueToProcessingQueueIfAvailable(Long userId);

    void updateQueue();

    void moveToProcessingQueue();

    void moveUserToProcessingQueue(Long userId);
}
