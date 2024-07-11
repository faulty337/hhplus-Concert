package com.hhp.concert.Business.Repository;

import com.hhp.concert.Business.Domain.WaitingQueue;

import java.util.Optional;

public interface WaitingRepository {
    public Optional<WaitingQueue> findByUserId(Long userId);

    public Optional<WaitingQueue> getFirst();

    WaitingQueue save(WaitingQueue waitingQueue);

    void deleteById(Long id);

    boolean existsById(Long id);

    Optional<WaitingQueue> findById(Long l);
}
