package com.hhp.concert.Interfaces;

import com.hhp.concert.Business.service.OutboxService;
import com.hhp.concert.Business.service.WaitingService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

@Configuration
@EnableScheduling
@RequiredArgsConstructor
public class SchedulerConfig {
    private final WaitingService waitingService;
    private final OutboxService outboxService;

    @Scheduled(fixedRate = 10000) // 10초마다 실행
    public void scheduleTokenService() {
        waitingService.updateQueue();
    }

    @Scheduled(fixedRate = 10000) // 10초마다 실행
    public void scheduleRetrySendMessage() {
        outboxService.retryOutboxEvents();
    }
}