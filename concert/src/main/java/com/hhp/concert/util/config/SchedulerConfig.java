package com.hhp.concert.util.config;

import com.hhp.concert.Business.service.waitingService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

@Configuration
@EnableScheduling
@RequiredArgsConstructor
public class SchedulerConfig {
    private final waitingService waitingService;

    @Scheduled(fixedRate = 10000) // 10초마다 실행
    public void scheduleTokenService() {
        waitingService.updateQueue();
    }
}