package com.hhp.concert.util;

import com.hhp.concert.Business.Domain.*;
import com.hhp.concert.Infrastructure.DBRepository.concert.ConcertJpaRepository;
import com.hhp.concert.Infrastructure.DBRepository.reservation.ReservationJpaRepository;
import com.hhp.concert.Infrastructure.DBRepository.seat.ConcertSeatJpaRepository;
import com.hhp.concert.Infrastructure.DBRepository.session.ConcertSessionJpaRepository;
import com.hhp.concert.Infrastructure.DBRepository.user.UserJpaRepository;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Table;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import net.datafaker.Faker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.annotation.Profile;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Profile("test")
@Component
@RequiredArgsConstructor
public class TestDatabaseManager implements InitializingBean {

    private static final Logger log = LoggerFactory.getLogger(TestDatabaseManager.class);
    @PersistenceContext
    private EntityManager entityManager;


    private final ConcertJpaRepository concertJpaRepository;

    private final ConcertSessionJpaRepository concertSessionJpaRepository;

    private final ConcertSeatJpaRepository concertSeatJpaRepository;
    private final ReservationJpaRepository reservationJpaRepository;

    private final UserJpaRepository userJpaRepository;


    private final JdbcTemplate jdbcTemplate;

    private final List<String> tables = new ArrayList<>();

    @Override
    public void afterPropertiesSet() {
        entityManager.getMetamodel().getEntities().stream()
                .filter(entityType -> entityType.getJavaType().isAnnotationPresent(Entity.class))
                .forEach(entityType -> {
                    Class<?> javaType = entityType.getJavaType();
                    Table tableAnnotation = javaType.getAnnotation(Table.class);
                    if (tableAnnotation != null) {
                        tables.add(tableAnnotation.name());
                    } else {
                        // Default to the entity name if @Table annotation is not present
                        tables.add(javaType.getSimpleName());
                    }
                });
    }

    @Transactional
    public void execute() {
        entityManager.flush();
        entityManager.createNativeQuery("SET REFERENTIAL_INTEGRITY FALSE").executeUpdate();
        for (String table : tables) {
            entityManager.createNativeQuery("TRUNCATE TABLE " + table).executeUpdate();
            entityManager.createNativeQuery("ALTER TABLE " + table + " ALTER COLUMN ID RESTART WITH 1").executeUpdate();
        }
        entityManager.createNativeQuery("SET REFERENTIAL_INTEGRITY TRUE").executeUpdate();
    }



    @Transactional
    public void init() {
        long startTime, endTime, duration;
        startTime = System.currentTimeMillis();
        Faker faker = new Faker();
        List<Concert> concertList = new ArrayList<>();
        List<ConcertSeat> seatList = new ArrayList<>();
        List<ConcertSession> sessionList = new ArrayList<>();
        List<Reservation> reservationList = new ArrayList<>();
        int batchSize = 1000;
        for (int i = 0; i < 10000; i++) {
            Concert concert = new Concert(faker.rockBand().name());
            concertList.add(concert);
            for (int j = 0; j < 10; j++) {
                ConcertSession session = new ConcertSession(faker.date().future(30, TimeUnit.DAYS).toLocalDateTime(), 1L);
                sessionList.add(session);
                for (int k = 0; k < 50; k++) {
                    ConcertSeat seat = new ConcertSeat(k, 10000, true, 1L);
                    Reservation reservation = new Reservation(1L, 1L, 1L, 1L, 3000);
                    seatList.add(seat);
                    reservationList.add(reservation);
                    if (seatList.size() % batchSize == 0) {
                        concertSeatJpaRepository.saveAll(seatList);
                        seatList.clear();
                        entityManager.flush();
                        entityManager.clear();
                    }
                    if (reservationList.size() % batchSize == 0) {
                        reservationJpaRepository.saveAll(reservationList);
                        reservationList.clear();
                        entityManager.flush();
                        entityManager.clear();
                    }
                }
                if (sessionList.size() % batchSize == 0) {
                    concertSessionJpaRepository.saveAll(sessionList);
                    sessionList.clear();
                    entityManager.flush();
                    entityManager.clear();
                }
            }
            if (concertList.size() % batchSize == 0) {
                concertJpaRepository.saveAll(concertList);
                concertList.clear();
                entityManager.flush();
                entityManager.clear();
            }
        }
        concertJpaRepository.saveAll(concertList);
        concertSeatJpaRepository.saveAll(seatList);
        concertSessionJpaRepository.saveAll(sessionList);
        reservationJpaRepository.saveAll(reservationList);
        endTime = System.currentTimeMillis();
        duration = endTime - startTime;
        log.info(" insert executed in {} ms", duration);
    }

}
