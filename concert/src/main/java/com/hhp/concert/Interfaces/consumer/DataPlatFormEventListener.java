package com.hhp.concert.Interfaces.consumer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class DataPlatFormEventListener {

    private static final Logger log = LoggerFactory.getLogger(DataPlatFormEventListener.class);

    @KafkaListener(topics = "concert-reserve-data", groupId = "${spring.kafka.consumer.group-id}")
    public void consume(Object message) {
        log.info("consume");
        log.info("Consumed message: {}", message);
    }

}
