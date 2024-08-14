package com.hhp.concert.Infrastructure.kafka;

import com.hhp.concert.Business.Domain.event.ReservationEvent;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ReservationKafkaMessageProducer {
    private static final Logger log = LoggerFactory.getLogger(ReservationKafkaMessageProducer.class);
    private final KafkaTemplate<String, Object> kafkaTemplate;

    public void send(String topic,ReservationEvent message) {
        log.info("sending payloa={} to topic={}", message, topic);
        kafkaTemplate.send(topic, message);
    }
}
