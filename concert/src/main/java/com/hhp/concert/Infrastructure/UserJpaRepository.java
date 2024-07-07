package com.hhp.concert.Infrastructure;

import com.hhp.concert.Business.Domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserJpaRepository extends JpaRepository<User, Long> {
    Optional<User> findById(Long username);
}
