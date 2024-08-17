package com.hhp.concert.Infrastructure.DBRepository.outbox;

import com.hhp.concert.Business.Domain.Outbox;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface OutboxJpaRepository extends JpaRepository<Outbox, Long> {
    Optional<Outbox> findByEventId(UUID eventId);
    List<Outbox> findAllByStatus(Outbox.OutboxStatus status);
}
