package com.hhp.concert.Business.Repository;

import com.hhp.concert.Business.Domain.Outbox;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface OutboxRepository {
    Outbox save(Outbox outbox);
    Optional<Outbox> findByEventId(UUID eventId);

    List<Outbox> findAllByStatus(Outbox.OutboxStatus outboxStatus);
}
