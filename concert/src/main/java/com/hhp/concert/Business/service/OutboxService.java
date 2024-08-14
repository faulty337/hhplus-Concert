package com.hhp.concert.Business.service;

import com.hhp.concert.Business.Domain.Outbox;

public interface OutboxService {
    Outbox initOutbox(String topic, Object message);
    void updatePublishedOutbox(Long outboxId);

    void retryOutboxEvents();
}
