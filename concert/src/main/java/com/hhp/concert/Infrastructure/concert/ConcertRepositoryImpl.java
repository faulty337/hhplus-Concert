package com.hhp.concert.Infrastructure.concert;

import com.hhp.concert.Business.Repository.ConcertRepository;
import com.hhp.concert.Business.Domain.Concert;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class ConcertRepositoryImpl implements ConcertRepository {
    private final ConcertJpaRepository concertjpaRepository;

    public boolean existsConcertId(Long concertId){
        return concertjpaRepository.existsById(concertId);
    }

    @Override
    public Optional<Concert> findById(Long concertId) {
        return concertjpaRepository.findById(concertId);
    }
}
