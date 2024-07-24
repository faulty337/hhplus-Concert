package com.hhp.concert.Business.service;

import com.hhp.concert.Business.Domain.User;
import com.hhp.concert.Business.Repository.UserRepository;
import com.hhp.concert.util.exception.CustomException;
import com.hhp.concert.util.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService{
    private final UserRepository userRepository;

    @Override
    public User getUser(Long userId) {
        return userRepository.findById(userId).orElseThrow(
                () -> new CustomException(ErrorCode.NOT_FOUND_USER_ID)
        );
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
    @Transactional
    public User chargePoint(Long userId, int amount) {
        //유효성 검사
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

    @Override
    public void checkUser(Long userId) {
        if(userRepository.findById(userId).isPresent()){
            throw new CustomException(ErrorCode.NOT_FOUND_USER_ID);
        }
    }
}
