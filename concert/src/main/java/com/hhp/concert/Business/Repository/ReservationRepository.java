package com.hhp.concert.Business.Repository;


import com.hhp.concert.Business.Domain.Reservation;

import java.util.Optional;

public interface ReservationRepository {
    Optional<Reservation> findById(Long id);

    Reservation save(Reservation reservation);
}
