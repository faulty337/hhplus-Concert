package com.hhp.concert.Infrastructure.DBRepository.reservation;

import com.hhp.concert.Business.Domain.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ReservationJpaRepository extends JpaRepository<Reservation, Long> {
    Optional<Reservation> findByIdAndUserId(long reservationId, long userId);
}
