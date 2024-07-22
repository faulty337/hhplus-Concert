package com.hhp.concert.concurrencyTest;

import com.hhp.concert.Infrastructure.user.UserJpaRepository;
import com.hhp.concert.Infrastructure.waitingQueue.WaitingQueueJpaRepository;
import com.hhp.concert.application.WaitingFacade;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class WaitingConcurrencyTest {

    private static final Logger logger = LoggerFactory.getLogger(WaitingConcurrencyTest.class);

    @Autowired
    private WaitingFacade waitingFacade;

    @Autowired
    private UserJpaRepository userRepository;

    @Autowired
    private WaitingQueueJpaRepository waitingQueueRepository;
    @Autowired
    private UserJpaRepository userJpaRepository;

//    @Test
//    public void getTokenConcurrencyTest(){
//
//        User user = userJpaRepository.save(new User(null, 0));
//        Long userId = user.getId();
//        logger.info("User saved with ID: {}", userId);
//        CompletableFuture.allOf(
//                CompletableFuture.runAsync(() -> waitingFacade.getToken(userId)),
//                CompletableFuture.runAsync(() -> waitingFacade.getToken(userId)),
//                CompletableFuture.runAsync(() -> waitingFacade.getToken(userId)),
//                CompletableFuture.runAsync(() -> waitingFacade.getToken(userId))
//        ).join();
//
//        int count = waitingQueueRepository.findAll().size();
//        assertEquals(1, 4);
//    }

}
