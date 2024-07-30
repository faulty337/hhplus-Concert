package com.hhp.concert.util;

import com.hhp.concert.Business.Domain.Concert;
import com.hhp.concert.Business.Domain.ConcertSeat;
import com.hhp.concert.Business.Domain.ConcertSession;
import com.hhp.concert.Business.Domain.User;
import com.hhp.concert.Infrastructure.concert.ConcertJpaRepository;
import com.hhp.concert.Infrastructure.seat.ConcertSeatJpaRepository;
import com.hhp.concert.Infrastructure.session.ConcertSessionJpaRepository;
import com.hhp.concert.Infrastructure.user.UserJpaRepository;
import jakarta.annotation.PostConstruct;
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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
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
        Faker faker = new Faker();
        List<ConcertSeat> seatList = new ArrayList<>();
        List<Long> sessionIdList = new ArrayList<>();
        log.info("insert start");

        for (int i = 0; i < 10000; i++) {
            Concert concert = new Concert(faker.rockBand().name());
            entityManager.persist(concert);
            if (i % 50 == 0) {
                entityManager.flush();
                entityManager.clear();
            }

            for (int j = 0; j < 10; j++) {
                ConcertSession session = new ConcertSession(faker.date().future(30, TimeUnit.DAYS).toLocalDateTime(), concert);
                entityManager.persist(session);
                if (j % 50 == 0) {
                    entityManager.flush();
                    entityManager.clear();
                }
                sessionIdList.add(session.getId());
            }
        }

        log.info("insert seat start");
        for (Long sessionId : sessionIdList) {
            for (int k = 0; k < 50; k++) {
                ConcertSeat seat = new ConcertSeat(k, 10000, true, sessionId);
                entityManager.persist(seat);
                if (k % 50 == 0) {
                    entityManager.flush();
                    entityManager.clear();
                }
            }
        }

        log.info("insert end");
    }

}
