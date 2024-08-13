package com.hhp.concert.event;



import com.hhp.concert.Infrastructure.kafka.ReservationKafkaMessageProducer;
import com.hhp.concert.Interfaces.consumer.OutboxEventListener;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;

@SpringBootTest
@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
@EmbeddedKafka(partitions = 1, controlledShutdown = false, brokerProperties = {"listeners=PLAINTEXT://localhost:9092", "port=9092"})
class KafkaEventTest {

    @Autowired
    private OutboxEventListener consumer;

    @Autowired
    private ReservationKafkaMessageProducer producer;

    @Test
    public void kafkaEventPingPongTest()
            throws Exception {

        String topic = "concert-reserve-data";


        int testCnt = 0;
        for (int i = 0; i < 9; i++) {
            producer.send(topic, "test");

            testCnt++;
        };

        // 모든 메시지를 수신할 때까지 기다립니다 , consumer latch 로 관리할 수 있습니다.
        consumer.getLatch().await(10, TimeUnit.SECONDS);

    }

}