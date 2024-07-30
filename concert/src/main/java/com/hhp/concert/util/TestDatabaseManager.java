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
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
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

    @PersistenceContext
    private EntityManager entityManager;


    private final ConcertJpaRepository concertJpaRepository;

    private final ConcertSessionJpaRepository concertSessionJpaRepository;

    private final ConcertSeatJpaRepository concertSeatJpaRepository;

    private final UserJpaRepository userJpaRepository;

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


    public void init() {
        Faker faker = new Faker();
        List<ConcertSeat> seats = new ArrayList<>();

        for (int i = 0; i < 100000; i++) {
            Concert concert = concertJpaRepository.save(new Concert(faker.rockBand().name()));

            for (int j = 0; j < 10; j++) {
                ConcertSession session = concertSessionJpaRepository.save(new ConcertSession(faker.date().future(30, TimeUnit.DAYS).toLocalDateTime(), concert));
                for (int k = 0; k < 50; k++) {
                    ConcertSeat seat = new ConcertSeat(k + 1, 100 + k * 10, true, session.getId());
                    seats.add(seat);
                }
                concertSeatJpaRepository.saveAll(seats);
            }


        }
    }
}
