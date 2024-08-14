package com.hhp.concert.Business.service;

import com.hhp.concert.Business.Domain.Reservation;
import com.hhp.concert.Business.Domain.event.ReservationEvent;

public interface ReservationService {
    Reservation createReservation(Reservation reservation);


    Reservation getReservationByUserId(long userId, long reservationId);

    void confirmReservationStatus(long reservationId);

    ReservationEvent createEvent(Long id);
}
