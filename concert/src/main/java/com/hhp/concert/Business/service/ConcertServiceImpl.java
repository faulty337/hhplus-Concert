package com.hhp.concert.Business.service;

import com.hhp.concert.Business.Domain.Concert;
import com.hhp.concert.Business.Repository.ConcertRepository;
import com.hhp.concert.util.CustomException;
import com.hhp.concert.util.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ConcertServiceImpl implements ConcertService{
    private final ConcertRepository concertRepository;

    @Override
    public Concert getConcert(Long concertId) {
        return concertRepository.findById(concertId).orElseThrow(
                () -> new CustomException(ErrorCode.NOT_FOUND_CONCERT_ID)
        );
    }
}
