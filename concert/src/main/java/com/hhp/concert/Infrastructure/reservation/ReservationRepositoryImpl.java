package com.hhp.concert.Infrastructure.reservation;

import com.hhp.concert.Business.Domain.Reservation;
import com.hhp.concert.Business.Repository.ReservationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class ReservationRepositoryImpl implements ReservationRepository {
    private final ReservationJpaRepository reservationJpaRepository;

    public Optional<Reservation> findById(Long id) {
        return reservationJpaRepository.findById(id);
    }

    @Override
    public Reservation save(Reservation reservation) {
        return reservationJpaRepository.save(reservation);
    }


    @Override
    public Optional<Reservation> findByIdAndUserId(long reservationId, long userId) {
        return reservationJpaRepository.findByIdAndUserId(reservationId, userId);
    }
}
