package com.hhp.concert.Business.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hhp.concert.Business.Domain.Outbox;
import com.hhp.concert.Business.Domain.event.ReservationEvent;
import com.hhp.concert.Business.Repository.OutboxRepository;
import com.hhp.concert.Infrastructure.kafka.ReservationMessageProducer;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OutboxServiceImpl implements OutboxService{
    private static final Logger log = LoggerFactory.getLogger(OutboxServiceImpl.class);
    private final OutboxRepository outboxRepository;
    private final ObjectMapper objectMapper;
    private final ReservationMessageProducer producer;

    @Override
    @Transactional
    public Outbox initOutbox(String topic, Object event) {
        Outbox outbox;
        try {
            String message = objectMapper.writeValueAsString(event);
            return outboxRepository.save(new Outbox(topic, message));
        }catch (JsonProcessingException e){
            log.error(e.getMessage());
            outbox = new Outbox(topic, e.getMessage());
            outbox.updateFailedStatus();
            return outboxRepository.save(outbox);
        }

    }

    @Override
    @Transactional
    public void updatePublishedOutbox(Long outboxId) {
        Outbox outbox = outboxRepository.findById(outboxId).orElse(null);
        if (outbox != null) {
            outbox.updatePublishStatus();
        }
    }


    @Transactional
    @Override
    public void retryOutboxEvents() {
        List<Outbox> outboxList = outboxRepository.findAllByStatus(Outbox.OutboxStatus.INIT);
        for (Outbox outbox : outboxList) {
            log.info("outbox createdAt : {}", outbox.getCreatedAt());
            //이후 실패한 메시지에 대해 Failed에 대한 조회가 필요하다면 상태에 대한 업데이트 필요
            //상태를 Failed로 바꾸는 것이기에 위 findAllByStatus에서 상태 조회에 대한 부담은 크게 없음
            if(outbox.getCreatedAt().isBefore(LocalDateTime.now().minusMinutes(5))) {
                log.info("Outbox event failed, OutboxId : {}, created at: {}, ", outbox.getId(), outbox.getCreatedAt());
                outbox.updateFailedStatus();
                continue;
            }
            try{
                ReservationEvent reservationEvent = objectMapper.readValue(outbox.getPayload(), ReservationEvent.class);
                reservationEvent.setOutboxId(outbox.getId());
                producer.send(outbox.getTopic(), reservationEvent);
            }catch (Exception e){
                log.error("RetryOutboxEvents Exception : {}", e.getMessage());
            }

        }
    }


}
