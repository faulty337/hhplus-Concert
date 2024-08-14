package com.hhp.concert.Infrastructure.kafka;

import com.hhp.concert.Business.Domain.event.ReservationEvent;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ReservationMessageProducer {
    private static final Logger log = LoggerFactory.getLogger(ReservationMessageProducer.class);
    private final KafkaTemplate<String, Object> kafkaTemplate;


    public void send(String topic,ReservationEvent message) {
        kafkaTemplate.send(topic, message);
    }
}
