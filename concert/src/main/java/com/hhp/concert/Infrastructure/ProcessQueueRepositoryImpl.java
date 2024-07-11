package com.hhp.concert.Infrastructure;

import com.hhp.concert.Business.Domain.ProcessQueue;
import com.hhp.concert.Business.ProcessQueueRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class ProcessQueueRepositoryImpl implements ProcessQueueRepository {
    private final ProcessQueueJpaRepository processQueueJpaRepository;


    public void delete(ProcessQueue processQueue){
        processQueueJpaRepository.delete(processQueue);
    }

    @Override
    public ProcessQueue save(ProcessQueue processQueue) {
        return processQueueJpaRepository.save(processQueue);
    }

    @Override
    public boolean existByUserId(Long userId) {
        return processQueueJpaRepository.existsByUserId(userId);
    }

    @Override
    public Long size() {
        return processQueueJpaRepository.count();
    }

    @Override
    public List<ProcessQueue> findAll() {
        return processQueueJpaRepository.findAll();
    }

    @Override
    public void deleteByUserId(Long userId) {
        processQueueJpaRepository.deleteByUserId(userId);
    }

}
