package com.hhp.concert.Business;

import com.hhp.concert.Business.Domain.ProcessQueue;

import java.util.List;

public interface ProcessQueueRepository {
    void delete(ProcessQueue processQueue);

    ProcessQueue save(ProcessQueue processQueue);

    boolean existByUserId(Long userId);

    Long size();

    List<ProcessQueue> findAll();
}
