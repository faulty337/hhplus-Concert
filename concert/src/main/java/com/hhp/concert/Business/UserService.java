package com.hhp.concert.Business;

import com.hhp.concert.Business.Domain.User;

import java.util.Optional;

public interface UserService {
    public Optional<User> getUser(Long userId);
}
