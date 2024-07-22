package com.hhp.concert.Business.Repository;

import com.hhp.concert.Business.Domain.ProcessQueue;

import java.util.List;

public interface ProcessQueueRepository {
    void delete(ProcessQueue processQueue);

    ProcessQueue save(ProcessQueue processQueue);

    boolean existByUserId(Long userId);

    Long size();

    List<ProcessQueue> findAll();

    void deleteByUserId(Long userId);
}
