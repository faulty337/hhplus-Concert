package com.hhp.concert.Business.service;

import com.hhp.concert.Business.Domain.User;
import com.hhp.concert.Business.Repository.UserRepository;
import com.hhp.concert.util.CustomException;
import com.hhp.concert.util.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService{
    private final UserRepository userRepository;

    @Override
    public Optional<User> getUser(Long userId) {
        return userRepository.findById(userId);
    }

    @Override
    public User updateToken(Long userId, String token) {
        User user = userRepository.findById(userId).orElseThrow(
                () -> new CustomException(ErrorCode.NOT_FOUND_USER_ID)
        );
        user.updateToken(token);
        return userRepository.save(user);
    }

    @Override
    public User chargePoint(Long userId, int amount) {
        User user = userRepository.findById(userId).orElseThrow(
                () -> new CustomException(ErrorCode.NOT_FOUND_USER_ID)
        );
        if(amount < 1){
            throw new CustomException(ErrorCode.INVALID_AMOUNT);
        }

        user.chargeBalance(amount);
        user = userRepository.save(user);
        return user;
    }
}
