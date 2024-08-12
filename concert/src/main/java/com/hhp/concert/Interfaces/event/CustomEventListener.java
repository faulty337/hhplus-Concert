package com.hhp.concert.Interfaces.event;

import com.hhp.concert.Business.Domain.event.DataPlatformSendEvent;
import com.hhp.concert.Infrastructure.kafka.ReservationKafkaMessageProducer;
import com.hhp.concert.util.exception.CustomException;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionalEventListener;

import java.util.Map;

@Component
@RequiredArgsConstructor
public class CustomEventListener {

    private final ReservationKafkaMessageProducer producer;

    private static final Logger log = LoggerFactory.getLogger(CustomEventListener.class);

    @Async
    @TransactionalEventListener
    public void handleCustomEvent(DataPlatformSendEvent event) {

        log.info("kafka send");
        producer.send("concert-reserve-data", "event");
    }
}