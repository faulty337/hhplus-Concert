package com.hhp.concert.Business.service;

import com.hhp.concert.Business.Domain.Reservation;
import com.hhp.concert.Business.Repository.ReservationRepository;
import com.hhp.concert.util.CustomException;
import com.hhp.concert.util.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ReservationServiceImpl implements ReservationService{
    private final ReservationRepository reservationRepository;

    @Override
    public Reservation addReservation(Reservation reservation) {
        return reservationRepository.save(reservation);
    }

    @Override
    public Reservation getReservationByUserId(long userId,long reservationId) {
        return reservationRepository.findByIdAndUserId(reservationId, userId).orElseThrow(
                ()-> new CustomException(ErrorCode.NOT_FOUND_RESERVATION_ID)
        );
    }
}
