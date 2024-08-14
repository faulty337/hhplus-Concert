package com.hhp.concert.cache;

import com.hhp.concert.Business.Domain.*;
import com.hhp.concert.Infrastructure.DBRepository.concert.ConcertJpaRepository;
import com.hhp.concert.Infrastructure.DBRepository.reservation.ReservationJpaRepository;
import com.hhp.concert.Infrastructure.DBRepository.seat.ConcertSeatJpaRepository;
import com.hhp.concert.Infrastructure.DBRepository.session.ConcertSessionJpaRepository;
import com.hhp.concert.Infrastructure.DBRepository.user.UserJpaRepository;
import com.hhp.concert.util.TestDatabaseManager;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@ActiveProfiles("test")
@SpringBootTest
public class QueryTest {
    private static final Logger log = LoggerFactory.getLogger(QueryTest.class);
    @Autowired
    private ConcertJpaRepository concertJpaRepository;

    @Autowired
    private ReservationJpaRepository reservationJpaRepository;

    @Autowired
    private ConcertSeatJpaRepository concertSeatJpaRepository;

    @Autowired
    private ConcertSessionJpaRepository concertSessionJpaRepository;

    @Autowired
    private UserJpaRepository userJpaRepository;

    @Autowired
    private TestDatabaseManager testDatabaseManager;

    @BeforeAll
    public static void setUp(@Autowired TestDatabaseManager testDatabaseManager) {
//        testDatabaseManager.init();
    }


    @Test
    public void testConcertQueryExecutionTime(){
        long startTime, endTime, duration;
        Concert concert = concertJpaRepository.save(new Concert("test"));
        startTime = System.currentTimeMillis();
        concertJpaRepository.findById(concert.getId());
        endTime = System.currentTimeMillis();
        duration = endTime - startTime;
        log.info("Concert findById executed in {} ms", duration);

        startTime = System.currentTimeMillis();
        concertJpaRepository.existsById(concert.getId());
        endTime = System.currentTimeMillis();
        duration = endTime - startTime;
        log.info("Concert existsById executed in {} ms", duration);
    }

    @Test
    public void testReservationQueryExecutionTime(){
        long startTime, endTime, duration;
        long userId = 213L;
        Reservation reservation = reservationJpaRepository.save(new Reservation(userId, 3L, 2L, 2L, 3000));
        startTime = System.currentTimeMillis();

        Reservation result = reservationJpaRepository.findById(reservation.getId()).get();

        endTime = System.currentTimeMillis();
        duration = endTime - startTime;
        log.info("reservation findById executed in {} ms", duration);


        startTime = System.currentTimeMillis();

        result = reservationJpaRepository.findByIdAndUserId(reservation.getId(), userId).get();

        endTime = System.currentTimeMillis();
        duration = endTime - startTime;
        log.info("reservation findByIdAndUserId executed in {} ms", duration);
    }

    @Test
    public void testSeatQueryExecutionTime() {
        long startTime, endTime, duration;
        long sessionId = 23;
        List<ConcertSeat> concertSeatList = new ArrayList<>();
        for(int i = 1; i < 30; i++){
            concertSeatList.add(new ConcertSeat(i, 3000, true, sessionId));
        }
        concertSeatJpaRepository.saveAll(concertSeatList);
        ConcertSeat concertSeat = concertSeatJpaRepository.save(new ConcertSeat(31, 3000, true, sessionId));
        startTime = System.currentTimeMillis();

        ConcertSeat result = concertSeatJpaRepository.findById(concertSeat.getId()).get();

        endTime = System.currentTimeMillis();
        duration = endTime - startTime;
        log.info("Seat findById executed in {} ms", duration);

        startTime = System.currentTimeMillis();

        result = concertSeatJpaRepository.findByIdAndConcertSessionId(concertSeat.getId(), sessionId).get();

        endTime = System.currentTimeMillis();
        duration = endTime - startTime;
        log.info("Seat findByIdAndConcertSessionId executed in {} ms", duration);

        startTime = System.currentTimeMillis();

        List<ConcertSeat> resultList = concertSeatJpaRepository.findAllByConcertSessionId(sessionId);

        endTime = System.currentTimeMillis();
        duration = endTime - startTime;
        log.info("Seat findAllByConcertSessionId executed in {} ms", duration);
    }

    @Test
    public void testSessionQueryExecutionTime() {
        long startTime, endTime, duration;
        long concertId = 321;
        ConcertSession session = concertSessionJpaRepository.save(new ConcertSession(LocalDateTime.now().plusDays(1), concertId));

        startTime = System.currentTimeMillis();

        List<ConcertSession> resultList = concertSessionJpaRepository.findAllByConcertIdAndSessionTimeAfter(concertId, LocalDateTime.now());

        endTime = System.currentTimeMillis();
        duration = endTime - startTime;
        log.info("Session findAllByConcertIdAndSessionTimeAfter executed in {} ms", duration);

        startTime = System.currentTimeMillis();

        ConcertSession result = concertSessionJpaRepository.findByIdAndConcertId(session.getId(), concertId).get();

        endTime = System.currentTimeMillis();
        duration = endTime - startTime;
        log.info("Session findByIdAndConcertId executed in {} ms", duration);

        startTime = System.currentTimeMillis();

        result = concertSessionJpaRepository.findByIdAndConcertIdAndSessionTimeAfter(session.getId(), concertId, LocalDateTime.now()).get();

        endTime = System.currentTimeMillis();
        duration = endTime - startTime;
        log.info("Session findByIdAndConcertIdAndSessionTimeAfter executed in {} ms", duration);
    }

    @Test
    public void testUserQueryExecutionTime() {
        long startTime, endTime, duration;
        long concertId = 321;
        long sessionId = 23;
        User user = userJpaRepository.save(new User("", 10000));

        startTime = System.currentTimeMillis();

        User result = userJpaRepository.findById(user.getId()).get();

        endTime = System.currentTimeMillis();
        duration = endTime - startTime;
        log.info("User findById executed in {} ms", duration);
    }
}
