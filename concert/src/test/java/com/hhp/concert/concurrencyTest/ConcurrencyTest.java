package com.hhp.concert.concurrencyTest;

import com.hhp.concert.Business.Domain.*;
import com.hhp.concert.Business.Repository.ReservationRepository;
import com.hhp.concert.Business.dto.ReservationResponseDto;
import com.hhp.concert.Business.service.*;
import com.hhp.concert.Infrastructure.concert.ConcertJpaRepository;
import com.hhp.concert.Infrastructure.payment.PaymentHistoryJpaRepository;
import com.hhp.concert.Infrastructure.reservation.ReservationJpaRepository;
import com.hhp.concert.Infrastructure.seat.ConcertSeatJpaRepository;
import com.hhp.concert.Infrastructure.session.ConcertSessionJpaRepository;
import com.hhp.concert.Infrastructure.user.UserJpaRepository;
import com.hhp.concert.Infrastructure.waitingQueue.WaitingQueueJpaRepository;
import com.hhp.concert.application.ConcertFacade;
import com.hhp.concert.application.PaymentFacade;
import com.hhp.concert.util.exception.CustomException;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
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
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

@SpringBootTest
public class ConcurrencyTest {

    private static final Logger logger = LoggerFactory.getLogger(ConcurrencyTest.class);

    @Autowired
    private PaymentHistoryJpaRepository paymentHistoryJpaRepository;
    @Autowired
    private UserJpaRepository userJpaRepository;
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
    @Autowired
    private PaymentFacade paymentFacade;

    @BeforeEach
    public void setUp() {
        reservationJpaRepository.deleteAll();
        concertSeatJpaRepository.deleteAll();
        concertSessionJpaRepository.deleteAll();
        concertJpaRepository.deleteAll();
        userJpaRepository.deleteAll();
    }

    @Test
    @DisplayName("충전 동시성 테스트")
    public void chargeConcurrencyTest() throws InterruptedException {
        User user = userJpaRepository.save(new User("", 0));
        Long userId = user.getId();
        int threadCount = 10;

        ExecutorService executorService = Executors.newFixedThreadPool(threadCount);
        CountDownLatch latch = new CountDownLatch(threadCount);


        for (int i = 1; i <= threadCount; i++) {
            executorService.submit(() -> {
                try {
                    paymentFacade.charge(userId, 1000);
                } catch(CustomException e) {
                    logger.info(e.getMessage());
                }finally {
                    latch.countDown();
                }
            });
        }

        latch.await();

        user = userJpaRepository.findById(user.getId()).get();
        assertEquals(threadCount * 1000, user.getBalance());


    }


    @Test
    public void reservationConcurrencyTest() throws InterruptedException {
        int threadCount = 10;
        //given
        List<User> userList = new ArrayList<>();
        for(int i = 1; i <= threadCount; i++) {
            userList.add(new User( "", 100000));
        }
        userJpaRepository.saveAll(userList);
        User user = userJpaRepository.save(new User("" ,100000));

        Concert concert = concertJpaRepository.save(new Concert("test"));
        ConcertSession concertSession = concertSessionJpaRepository.save(new ConcertSession(LocalDateTime.now().plusDays(1), concert));

        List<ConcertSeat> concertSeatList = new ArrayList<>();

        long listSize = 5L;
        int seatNum = 1;
        for(;seatNum <= listSize; seatNum++){
            concertSeatList.add(new ConcertSeat(seatNum, 1000, false, concertSession));
        }
        int seatNumber = seatNum;
        Long seatId = 33L;
        concertSeatJpaRepository.saveAll(concertSeatList);

        ConcertSeat concertSeat = concertSeatJpaRepository.save(new ConcertSeat(seatId, seatNumber, 1000, true, concertSession));



        ExecutorService executorService = Executors.newFixedThreadPool(threadCount);
        CountDownLatch latch = new CountDownLatch(threadCount);

        AtomicInteger userIdCounter = new AtomicInteger(1);

        for (int i = 1; i <= threadCount; i++) {
            executorService.submit(() -> {
                int userId = userIdCounter.getAndIncrement();
                try {
                    concertFacade.reservation(concert.getId(), concertSession.getId(), concertSeat.getId(), (long) userId);
                }catch(CustomException e) {
                    logger.info(e.getMessage());
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await();

        List<Reservation> reservationList = reservationJpaRepository.findAll();

        assertEquals(1, reservationList.size());
    }

    @Test
    public void paymentConcurrencyTest() throws InterruptedException {
        int threadCount = 10;
        //given
        List<User> userList = new ArrayList<>();
        for(int i = 1; i <= threadCount; i++) {
            userJpaRepository.save(new User( "", 100000));
        }
        User user =userJpaRepository.save(new User("", 10000));



        Concert concert = concertJpaRepository.save(new Concert("test"));
        ConcertSession concertSession = concertSessionJpaRepository.save(new ConcertSession(LocalDateTime.now().plusDays(1), concert));

        long listSize = 5L;
        int seatNum = 1;
        for(;seatNum <= listSize; seatNum++){
            concertSeatJpaRepository.save(new ConcertSeat(seatNum, 1000, false, concertSession));
        }
        int seatNumber = seatNum;
        Long seatId = 1241241L;
        ConcertSeat concertSeat = concertSeatJpaRepository.save(new ConcertSeat(seatId, seatNumber, 1000, true, concertSession));

        Reservation reservation = reservationJpaRepository.save(new Reservation(user.getId(), concertSession.getId(), concertSeat.getId(), concertSeat.getPrice()));

        ExecutorService executorService = Executors.newFixedThreadPool(threadCount);
        CountDownLatch latch = new CountDownLatch(threadCount);

        for (int i = 1; i <= threadCount; i++) {
            executorService.submit(() -> {
                try {
                    paymentFacade.payment(user.getId(), reservation.getId());
                }catch(CustomException e) {
                    logger.info(e.getMessage());
                }
                finally {
                    latch.countDown();
                }
            });
        }

        latch.await();

        List<PaymentHistory> paymentHistoryList = paymentHistoryJpaRepository.findAllByUserId(user.getId());

        assertEquals(1, paymentHistoryList.size());


    }

}
