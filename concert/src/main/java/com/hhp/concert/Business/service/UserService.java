package com.hhp.concert.Business.service;

import com.hhp.concert.Business.Domain.User;

import java.util.Optional;

public interface UserService {
    public User getUser(Long userId);

    public User updateToken(Long userId, String token);

    public User chargePoint(Long userId, int point);

    public void checkUser(Long userId);

    User usePoint(long id, int amount);
}
