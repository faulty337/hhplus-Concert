package com.hhp.concert.Interfaces.event;

import com.hhp.concert.Business.Domain.DataPlatformSendEvent;
import com.hhp.concert.util.exception.CustomException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionalEventListener;

import java.util.Map;

@Component
public class CustomEventListener {

    private static final Logger log = LoggerFactory.getLogger(CustomEventListener.class);

    @Async
    @TransactionalEventListener
    public void handleCustomEvent(DataPlatformSendEvent event) throws InterruptedException {
        log.info("handleCustomEvent");
        Thread.sleep(10000);
        try {
            //플랫폼 로직

            log.info(event.toString());
            throw new Exception("플랫폼 에러");
        }catch (Exception e){
            log.error("플랫폼 전송 오류");
        }
    }
}