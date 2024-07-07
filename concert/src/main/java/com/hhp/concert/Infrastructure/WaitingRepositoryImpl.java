package com.hhp.concert.Infrastructure;

import com.hhp.concert.Business.Domain.WaitingQueue;
import com.hhp.concert.Business.Repository.WaitingRepository;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@AllArgsConstructor
public class WaitingRepositoryImpl implements WaitingRepository {
    private WaitingQueueJpaRepository waitingQueueJpaRepository;

    @Override
    public Optional<WaitingQueue> findByUserId(Long userId) {
        return waitingQueueJpaRepository.findByUserId(userId);
    }

    @Override
    public Optional<WaitingQueue> getFirst() {
        return waitingQueueJpaRepository.findFirstByOrderByCreatedAtAsc();
    }

    @Override
    public WaitingQueue save(WaitingQueue waitingQueue) {
        return waitingQueueJpaRepository.save(waitingQueue);
    }


}
