package com.hhp.concert.Infrastructure;

import com.hhp.concert.Business.Repository.ConcertRepository;
import com.hhp.concert.Business.Domain.Concert;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class RepositoryImpl implements ConcertRepository {
    private final ConcertJpaRepository jpaRepository;

    public boolean existsConcertId(Long concertId){
        return jpaRepository.existsById(concertId);
    }

    @Override
    public Optional<Concert> findById(Long concertId) {
        return jpaRepository.findById(concertId);
    }
}
