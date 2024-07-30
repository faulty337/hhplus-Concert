package com.hhp.concert.Infrastructure.user;

import com.hhp.concert.Business.Domain.User;
import com.hhp.concert.Business.Repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserRepositoryImpl implements UserRepository {

    private final UserJpaRepository userJpaRepository;

    @Override
    public Optional<User> findById(long userId) {
        return userJpaRepository.findById(userId);
    }

    @Override
    public User save(User user) {
        return userJpaRepository.save(user);
    }

    @Override
    public Optional<User> findByIdWithLock(Long userId) {
        return userJpaRepository.findByIdWithLock(userId);
    }
}
