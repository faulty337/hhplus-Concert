package com.hhp.concert.Business.Repository;

import com.hhp.concert.Business.Domain.Concert;

import java.util.Optional;

public interface ConcertRepository {
    public boolean existsConcertId(Long concertId);

    Optional<Concert> findById(Long concertId);
}
