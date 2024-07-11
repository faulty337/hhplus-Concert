package com.hhp.concert.Infrastructure;

import com.hhp.concert.Business.Domain.ProcessQueue;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProcessQueueJpaRepository extends JpaRepository<ProcessQueue, Long> {
    boolean existsByUserId(Long userId);
}
