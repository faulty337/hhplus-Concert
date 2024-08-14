package com.hhp.concert.Business.service;

import com.hhp.concert.Business.Domain.Concert;
import com.hhp.concert.Business.Domain.ConcertSeat;
import com.hhp.concert.Business.Domain.Reservation;
import com.hhp.concert.Business.Domain.event.ReservationEvent;
import com.hhp.concert.Business.Repository.ConcertRepository;
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
    private final ConcertRepository concertRepository;

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

    @Override
    public ReservationEvent createEvent(Long id) {
        Reservation reservation = reservationRepository.findById(id).orElseThrow(
                () -> new CustomException(ErrorCode.NOT_FOUND_RESERVATION_ID)
        );
        ConcertSeat concertSeat = concertSeatRepository.findById(reservation.getConcertSeatId()).orElseThrow(
                () -> new CustomException(ErrorCode.NOT_FOUND_SEAT_ID)
        );
        Concert concert = concertRepository.findById(reservation.getConcertId()).orElseThrow(
                () -> new CustomException(ErrorCode.NOT_FOUND_CONCERT_ID)
        );

        return new ReservationEvent(concert.getId(), concert.getTitle(), concertSeat.getId(), concertSeat.getSeatNumber());
    }
}
