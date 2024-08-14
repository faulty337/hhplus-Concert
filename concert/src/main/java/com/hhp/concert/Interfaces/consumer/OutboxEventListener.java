package com.hhp.concert.Interfaces.consumer;

import com.hhp.concert.Business.Domain.event.ReservationEvent;
import lombok.Getter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.concurrent.CountDownLatch;

@Component
@Getter
public class OutboxEventListener {
    private CountDownLatch latch = new CountDownLatch(10);
    private static final Logger log = LoggerFactory.getLogger(OutboxEventListener.class);

    @KafkaListener(topics = "concert-reserve-data")
    public void consume(ReservationEvent message) {
        message.outboxId();
        log.info("Consumed message: {}", message);
    }

    @KafkaListener(topics = "concert-reserve-data", groupId = "test")
    public void consumeTest(ReservationEvent message){
        log.info("test");
    }

    public void resetLatch() {
        CountDownLatch latch = new CountDownLatch(1);
    }


}
