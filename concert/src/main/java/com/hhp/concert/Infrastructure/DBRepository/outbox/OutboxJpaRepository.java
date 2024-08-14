package com.hhp.concert.Infrastructure.DBRepository.outbox;

import com.hhp.concert.Business.Domain.Outbox;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface OutboxJpaRepository extends JpaRepository<Outbox, Long> {

    List<Outbox> findAllByStatus(Outbox.OutboxStatus status);
}
