package com.hhp.concert.concurrencyTest;

import com.hhp.concert.Business.Domain.*;
import com.hhp.concert.Business.Repository.ReservationRepository;
import com.hhp.concert.Business.service.*;
import com.hhp.concert.Infrastructure.concert.ConcertJpaRepository;
import com.hhp.concert.Infrastructure.reservation.ReservationJpaRepository;
import com.hhp.concert.Infrastructure.seat.ConcertSeatJpaRepository;
import com.hhp.concert.Infrastructure.session.ConcertSessionJpaRepository;
import com.hhp.concert.Infrastructure.user.UserJpaRepository;
import com.hhp.concert.Infrastructure.waitingQueue.WaitingQueueJpaRepository;
import com.hhp.concert.application.ConcertFacade;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

@SpringBootTest
public class ConcurrencyTest {

    private static final Logger logger = LoggerFactory.getLogger(ConcurrencyTest.class);

    @Autowired
    private ReservationService reservationService;
    @Autowired
    private UserJpaRepository userJpaRepository;
    @Autowired
    private ReservationRepository reservationRepository;
    @Autowired
    private WaitingQueueJpaRepository waitingQueueJpaRepository;
    @Autowired
    private ConcertJpaRepository concertJpaRepository;
    @Autowired
    private ConcertSessionJpaRepository concertSessionJpaRepository;
    @Autowired
    private ConcertSeatJpaRepository concertSeatJpaRepository;
    @Autowired
    private ReservationJpaRepository reservationJpaRepository;
    @Autowired
    private ConcertFacade concertFacade;

    private Long concertId = 1L;
    private Long sessionId = 1L;
    private Long seatId = 1L;

    @BeforeEach
    public void setUp() {

    }
    @Test
    public void getTokenConcurrencyTest() throws InterruptedException {
        int threadCount = 10;
        //given
        List<User> userList = new ArrayList<>();
        for(int i = 1; i <= threadCount; i++) {
            userList.add(new User(i, "", 100000));
        }
        userJpaRepository.saveAll(userList);
        Concert concert = concertJpaRepository.save(new Concert("test"));
        ConcertSession concertSession = concertSessionJpaRepository.save(new ConcertSession(LocalDateTime.now().plusDays(1), concert));

        List<ConcertSeat> concertSeatList = new ArrayList<>();

        long listSize = 5L;
        int seatNum = 1;
        for(;seatNum <= listSize; seatNum++){
            concertSeatList.add(new ConcertSeat(seatNum, 1000, false, concertSession));
        }
        int seatNumber = seatNum;
        ConcertSeat concertSeat = new ConcertSeat(seatNumber, 1000, true, concertSession);
        concertSeatList.add(concertSeat);
        concertSeatJpaRepository.saveAll(concertSeatList);




        ExecutorService executorService = Executors.newFixedThreadPool(threadCount);
        CountDownLatch latch = new CountDownLatch(threadCount);

        AtomicInteger userIdCounter = new AtomicInteger(1);

        for (int i = 1; i <= threadCount; i++) {
            executorService.submit(() -> {
                int userId = userIdCounter.getAndIncrement();
                try {
                    concertFacade.reservation(concert.getId(), concertSession.getId(), concertSeat.getId(), (long) userId);
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await();

        List<Reservation> reservationList = reservationJpaRepository.findAll();

        assertEquals(1, reservationList.size());


    }

}
