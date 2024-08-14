package com.hhp.concert.Business.Repository;

import com.hhp.concert.Business.Domain.Outbox;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface OutboxRepository {
    Outbox save(Outbox outbox);
    Optional<Outbox> findById(Long id);

    List<Outbox> findAllByStatus(Outbox.OutboxStatus outboxStatus);
}
