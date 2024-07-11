package com.hhp.concert.Business;

import com.hhp.concert.Business.Domain.ProcessQueue;

public interface ProcessQueueRepository {
    void delete(Long processionId);

    ProcessQueue save(ProcessQueue processQueue);

    boolean existByUserId(Long userId);
}
