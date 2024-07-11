package com.hhp.concert.Infrastructure;

import com.hhp.concert.Business.Domain.ProcessQueue;
import com.hhp.concert.Business.ProcessQueueRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class ProcessQueueRepositoryImpl implements ProcessQueueRepository {
    private final ProcessQueueJpaRepository processQueueJpaRepository;


    public void delete(Long processionId){
        processQueueJpaRepository.deleteById(processionId);
    }

    @Override
    public ProcessQueue save(ProcessQueue processQueue) {
        return processQueueJpaRepository.save(processQueue);
    }

    @Override
    public boolean existByUserId(Long userId) {
        return processQueueJpaRepository.existsByUserId(userId);
    }
}
