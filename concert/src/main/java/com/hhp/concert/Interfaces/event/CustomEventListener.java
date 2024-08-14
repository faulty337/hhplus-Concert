package com.hhp.concert.Interfaces.event;

import com.hhp.concert.Business.Domain.event.ReservationEvent;
import com.hhp.concert.Business.Repository.OutboxRepository;
import com.hhp.concert.Business.service.OutboxService;
import com.hhp.concert.Infrastructure.kafka.ReservationKafkaMessageProducer;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
public class CustomEventListener {

    private final ReservationKafkaMessageProducer producer;
    private final OutboxService outboxService;

    private static final Logger log = LoggerFactory.getLogger(CustomEventListener.class);

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void reservationDataSand(ReservationEvent event) {
        producer.send("concert-reserve-data", event);
    }

    @Async
    @TransactionalEventListener(phase = TransactionPhase.BEFORE_COMMIT)
    public void initOutbox(ReservationEvent event) {
        outboxService.initOutbox("concert-reserve-data", event);
    }

}