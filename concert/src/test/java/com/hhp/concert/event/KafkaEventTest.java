package com.hhp.concert.event;



import com.hhp.concert.Business.Domain.Outbox;
import com.hhp.concert.Business.Domain.event.ReservationEvent;
import com.hhp.concert.Business.Repository.OutboxRepository;
import com.hhp.concert.Business.service.OutboxService;
import com.hhp.concert.Infrastructure.kafka.ReservationMessageProducer;
import com.hhp.concert.Interfaces.consumer.OutboxEventListener;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;

@SpringBootTest
@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
@EmbeddedKafka(partitions = 1, controlledShutdown = false, brokerProperties = {"listeners=PLAINTEXT://localhost:9092", "port=9092"})
class KafkaEventTest {

    @Autowired
    private OutboxEventListener consumer;

    @Autowired
    private ReservationMessageProducer producer;
    @Autowired
    private OutboxService outboxService;
    @Autowired
    private OutboxRepository outboxRepository;

    @Test
    @DisplayName("Kafka Producer, Consumer Test")
    public void kafkaEventPingPongTest()
            throws Exception {

        String topic = "concert-reserve-data";

        ReservationEvent reservationEvent = new ReservationEvent(1L, "title", 1L, 1);

        Outbox outbox = outboxService.initOutbox(topic, reservationEvent);
        reservationEvent.setOutboxId(outbox.getId());

        producer.send(topic, reservationEvent);

        consumer.getLatch().await(10, TimeUnit.SECONDS);

        assertEquals(Outbox.OutboxStatus.PUBLISH, Objects.requireNonNull(outboxRepository.findById(outbox.getId()).orElse(null)).getStatus());

    }

    @Test
    @DisplayName("Outbox 스케줄러 테스트")
    public void OutboxRetryTest() throws InterruptedException {

        String topic = "concert-reserve-data";

        ReservationEvent reservationEvent = new ReservationEvent(1L, "title", 1L, 1);

        Outbox outbox = outboxService.initOutbox(topic, reservationEvent);

        outboxService.retryOutboxEvents();

        consumer.getLatch().await(10, TimeUnit.SECONDS);

        assertEquals(Outbox.OutboxStatus.PUBLISH, Objects.requireNonNull(outboxRepository.findById(outbox.getId()).orElse(null)).getStatus());
    }

    @Test
    @DisplayName("Outbox TimeOut 테스트")
    public void OutboxFiledTest() throws InterruptedException {
        String topic = "concert-reserve-data";

        ReservationEvent reservationEvent = new ReservationEvent(1L, "title", 1L, 1);

        Outbox outbox = outboxService.initOutbox(topic, reservationEvent);

        outbox.setCreatedAt(LocalDateTime.now().minusDays(6));
        outboxRepository.save(outbox);

        outboxService.retryOutboxEvents();

        assertEquals(Outbox.OutboxStatus.FAILED, Objects.requireNonNull(outboxRepository.findById(outbox.getId()).orElse(null)).getStatus());
    }

}