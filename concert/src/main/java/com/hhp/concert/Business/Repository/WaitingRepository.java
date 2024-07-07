package com.hhp.concert.Business.Repository;

import com.hhp.concert.Business.Domain.WaitingQueue;

import java.util.Optional;

public interface WaitingRepository {
    public Optional<WaitingQueue> findByUserId(Long userId);

}
