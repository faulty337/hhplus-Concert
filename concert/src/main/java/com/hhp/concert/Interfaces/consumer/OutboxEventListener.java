package com.hhp.concert.Interfaces.consumer;

import lombok.Getter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;


@Component
@Getter
public class OutboxEventListener {
    private static final Logger log = LoggerFactory.getLogger(OutboxEventListener.class);

    @KafkaListener(topics = "concert-reserve-data")
    public void consume(String message) {
        log.info("consume");
        log.info("Consumed message: {}", message);
    }


}
