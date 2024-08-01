package com.hhp.concert.Infrastructure.DBRepository.processQueue;

import com.hhp.concert.Business.Domain.ProcessQueue;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProcessQueueJpaRepository extends JpaRepository<ProcessQueue, Long> {
    boolean existsByUserId(Long userId);

    void deleteByUserId(Long userId);
}
