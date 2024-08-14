package com.hhp.concert.Interfaces.event;

import com.hhp.concert.Business.Domain.Outbox;
import com.hhp.concert.Business.Domain.event.ReservationEvent;
import com.hhp.concert.Business.service.OutboxService;
import com.hhp.concert.Infrastructure.kafka.ReservationMessageProducer;
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

    private final ReservationMessageProducer producer;
    private final OutboxService outboxService;

    private static final Logger log = LoggerFactory.getLogger(CustomEventListener.class);

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void reservationDataSand(ReservationEvent event) {
        Outbox outbox = outboxService.initOutbox("concert-reserve-data", event);
        event.setOutboxId(outbox.getId());
        producer.send("concert-reserve-data", event);
    }


    //비동기 이벤트 분리 시 outbox에 대한 Id를 위에 send에 넣을 수 없음
//    @Async
//    @TransactionalEventListener(phase = TransactionPhase.BEFORE_COMMIT)
//    public void initOutbox(ReservationEvent event) {
//        Outbox outbox = outboxService.initOutbox("concert-reserve-data", event);
//    }

}