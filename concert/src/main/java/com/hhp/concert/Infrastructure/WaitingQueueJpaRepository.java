package com.hhp.concert.Infrastructure;


import com.hhp.concert.Business.Domain.WaitingQueue;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface WaitingQueueJpaRepository extends JpaRepository<WaitingQueue, Long> {
    Optional<WaitingQueue> findByUserId(Long userId);

    Optional<WaitingQueue> findFirstByOrderByCreatedAtAsc();
}
