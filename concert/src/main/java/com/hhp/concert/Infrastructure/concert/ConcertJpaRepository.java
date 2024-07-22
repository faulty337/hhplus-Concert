package com.hhp.concert.Infrastructure.concert;

import com.hhp.concert.Business.Domain.Concert;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ConcertJpaRepository extends JpaRepository<Concert, Long> {
}
