package com.hhp.concert.cache;

import com.hhp.concert.Business.Domain.Concert;
import com.hhp.concert.Infrastructure.concert.ConcertJpaRepository;
import com.hhp.concert.Infrastructure.reservation.ReservationJpaRepository;
import com.hhp.concert.Infrastructure.seat.ConcertSeatJpaRepository;
import com.hhp.concert.Infrastructure.session.ConcertSessionJpaRepository;
import com.hhp.concert.Infrastructure.user.UserJpaRepository;
import com.hhp.concert.util.TestDatabaseManager;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Profile;
import org.springframework.test.context.ActiveProfiles;

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
        testDatabaseManager.init();
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
}
