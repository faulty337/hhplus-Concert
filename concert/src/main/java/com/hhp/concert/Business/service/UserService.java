package com.hhp.concert.Business.service;

import com.hhp.concert.Business.Domain.User;

import java.util.Optional;

public interface UserService {
    public Optional<User> getUser(Long userId);

    public User updateToken(Long userId, String token);
}
