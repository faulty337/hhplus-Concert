package com.hhp.concert.Business;

import com.hhp.concert.Business.Domain.Concert;

public interface ConcertService {
    public Concert getConcert(Long concertId);
}
