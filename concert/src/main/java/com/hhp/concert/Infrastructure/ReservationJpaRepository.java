package com.hhp.concert.Infrastructure;

import com.hhp.concert.Business.Domain.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReservationJpaRepository extends JpaRepository<Reservation, Long> {
}
