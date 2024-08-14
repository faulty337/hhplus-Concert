package com.hhp.concert.Interfaces.consumer;

import com.hhp.concert.Business.Domain.event.ReservationEvent;
import com.hhp.concert.Business.Repository.OutboxRepository;
import com.hhp.concert.Business.service.OutboxService;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.concurrent.CountDownLatch;

@Component
@Getter
@RequiredArgsConstructor
public class OutboxEventListener {
    private CountDownLatch latch = new CountDownLatch(1);
    private static final Logger log = LoggerFactory.getLogger(OutboxEventListener.class);
    private final OutboxService outboxService;

    @KafkaListener(topics = "concert-reserve-data")
    public void consume(ReservationEvent event) {
        log.info("consumed event: {}", event.toString());
        outboxService.updatePublishedOutbox(event.getOutboxId());
    }


}
