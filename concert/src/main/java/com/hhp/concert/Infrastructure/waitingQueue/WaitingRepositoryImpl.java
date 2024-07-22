package com.hhp.concert.Infrastructure.waitingQueue;

import com.hhp.concert.Business.Domain.WaitingQueue;
import com.hhp.concert.Business.Repository.WaitingRepository;
import lombok.AllArgsConstructor;
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

    @Override
    public void deleteById(Long id) {
        waitingQueueJpaRepository.deleteById(id);
    }

    @Override
    public void delete(WaitingQueue waitingQueue) {
        waitingQueueJpaRepository.delete(waitingQueue);
    }

    @Override
    public boolean existsById(Long id) {
        return waitingQueueJpaRepository.existsById(id);
    }

    @Override
    public Optional<WaitingQueue> findById(Long id) {
        return waitingQueueJpaRepository.findById(id);
    }

    @Override
    public Long count() {
        return waitingQueueJpaRepository.count();
    }


}
