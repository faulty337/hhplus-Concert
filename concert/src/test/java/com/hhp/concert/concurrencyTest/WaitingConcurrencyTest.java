package com.hhp.concert.concurrencyTest;

import com.hhp.concert.Business.Domain.*;
import com.hhp.concert.Infrastructure.user.UserJpaRepository;
import com.hhp.concert.Infrastructure.waitingQueue.WaitingQueueJpaRepository;
import com.hhp.concert.application.ConcertFacade;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;

@SpringBootTest
public class WaitingConcurrencyTest {

    private static final Logger logger = LoggerFactory.getLogger(WaitingConcurrencyTest.class);

    @Autowired
    private ConcertFacade concertFacade;

    @Autowired
    private UserJpaRepository userRepository;

    @Autowired
    private WaitingQueueJpaRepository waitingQueueRepository;
    @Autowired
    private UserJpaRepository userJpaRepository;

    @Test
    public void getTokenConcurrencyTest(){

        long userId = 1L;
        Long concertId = 1L;
        Long sessionId = 1L;
        Long seatId = 2L;
        Long reservationId = 23L;
        User user = new User(userId, null, 1000);
        Concert concert = new Concert(concertId, "test");
        ConcertSession concertSession = new ConcertSession(sessionId, LocalDateTime.now(), concert);
        ConcertSeat concertSeat = new ConcertSeat(seatId, 1, 1000, false, concertSession);
        Reservation reservation = new Reservation(reservationId, user, concertSession, concertSeat, concertSeat.getPrice(), Reservation.ReservationStatus.PENDING);
//
//        given(concertService.getConcert(concertId)).willReturn(concert);
//        given(sessionService.getSessionByOpenAndConcertId(concertId, sessionId)).willReturn(concertSession);
//        given(seatService.getSeatsForConcertSessionAndAvailable(sessionId, seatId)).willReturn(concertSeat);
//        given(userService.getUser(userId)).willReturn(Optional.of(user));
//        given(reservationService.addReservation(any(Reservation.class))).willReturn(reservation);
//
//        User user = userJpaRepository.save(new User(null, 0));
//        Long userId = user.getId();
        logger.info("User saved with ID: {}", userId);
//        CompletableFuture.allOf(
//                CompletableFuture.runAsync(() -> concertFacade.getToken(userId)),
//                CompletableFuture.runAsync(() -> concertFacade.getToken(userId)),
//                CompletableFuture.runAsync(() -> concertFacade.getToken(userId)),
//                CompletableFuture.runAsync(() -> concertFacade.getToken(userId))
//        ).join();

        int count = waitingQueueRepository.findAll().size();
        assertEquals(1, 4);
    }

}
