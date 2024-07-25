package com.hhp.concert.Business.service;

import com.hhp.concert.Business.Domain.ConcertSeat;
import com.hhp.concert.Business.Domain.Reservation;
import com.hhp.concert.Business.Repository.ConcertSeatRepository;
import com.hhp.concert.Business.Repository.ConcertSessionRepository;
import com.hhp.concert.Business.Repository.ReservationRepository;
import com.hhp.concert.util.exception.CustomException;
import com.hhp.concert.util.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ReservationServiceImpl implements ReservationService{
    private final ReservationRepository reservationRepository;
    private final ConcertSeatRepository concertSeatRepository;

    @Override
    public Reservation createReservation(Reservation reservation) {
        ConcertSeat concertSeat = concertSeatRepository.findById(reservation.getConcertSeatId()).orElseThrow(
                () -> new CustomException(ErrorCode.NOT_FOUND_SEAT_ID)
        );
        concertSeat.reserveSeat();
        concertSeatRepository.save(concertSeat);
        return reservationRepository.save(reservation);
    }

    @Override
    public Reservation getReservationByUserId(long userId,long reservationId) {
        return reservationRepository.findByIdAndUserId(reservationId, userId).orElseThrow(
                ()-> new CustomException(ErrorCode.NOT_FOUND_RESERVATION_ID)
        );
    }

    @Override
    @Transactional
    public void confirmReservationStatus(long reservationId) {
        Reservation reservation = reservationRepository.findById(reservationId).orElseThrow(
                ()-> new CustomException(ErrorCode.NOT_FOUND_RESERVATION_ID)
        );
        reservation.setStatus(Reservation.ReservationStatus.CONFIRMED);
        reservationRepository.save(reservation);
    }
}
