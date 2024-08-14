package com.hhp.concert.Infrastructure.DBRepository.outbox;

import com.hhp.concert.Business.Domain.Outbox;
import com.hhp.concert.Business.Repository.OutboxRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class OutboxRepositoryImpl implements OutboxRepository {
    private final OutboxJpaRepository outboxJpaRepository;

    @Override
    public Outbox save(Outbox outbox) {
        return outboxJpaRepository.save(outbox);
    }

    @Override
    public Optional<Outbox> findById(Long id) {
        return outboxJpaRepository.findById(id);
    }

    @Override
    public List<Outbox> findAllByStatus(Outbox.OutboxStatus status) {
        return outboxJpaRepository.findAllByStatus(status);
    }
}
