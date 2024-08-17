package com.hhp.concert.Business.service;

import com.hhp.concert.Business.Domain.Outbox;

import java.util.UUID;

public interface OutboxService {
    Outbox initOutbox(String topic, Object message, UUID eventId);
    void updatePublishedOutbox(UUID eventId);

    void retryOutboxEvents();
}
