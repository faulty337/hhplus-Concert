package com.hhp.concert.Business.Repository;

import com.hhp.concert.Business.Domain.User;

import java.util.Optional;

public interface UserRepository {
    public Optional<User> findById(long userId);

    public User save(User user);
}
