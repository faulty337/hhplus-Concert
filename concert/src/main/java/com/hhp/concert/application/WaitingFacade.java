package com.hhp.concert.application;

import com.hhp.concert.Business.Domain.User;
import com.hhp.concert.Business.Domain.WaitingQueue;
import com.hhp.concert.Business.service.JwtService;
import com.hhp.concert.Business.service.UserService;
import com.hhp.concert.Business.service.WaitingService;
import com.hhp.concert.Business.dto.GetTokenResponseDto;
import com.hhp.concert.Business.dto.GetWaitingTokenResponseDto;
import com.hhp.concert.util.CustomException;
import com.hhp.concert.util.ErrorCode;
import com.hhp.concert.util.JwtUtil;
import com.hhp.concert.util.enums.QueueKey;
import com.hhp.concert.util.enums.QueueType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class WaitingFacade {
    private final JwtService jwtService;
    private final WaitingService waitingService;
    private final UserService userService;


    @Transactional
    public GetTokenResponseDto getToken(Long userId){
        Optional<WaitingQueue> waitingQueue = waitingService.findByUserId(userId);
        if(waitingQueue.isPresent()){
            User user = userService.getUser(waitingQueue.get().getUserId()).orElseThrow(
                    () -> new CustomException(ErrorCode.NOT_FOUND_USER_ID)
            );
            return new GetTokenResponseDto(user.getWaitingToken());
        }

        String token = jwtService.createWaitingToken(userId);


        userService.updateToken(userId, token);
        waitingService.add(new WaitingQueue(userId));

        return new GetTokenResponseDto(token);
    }

    @Transactional
    public GetWaitingTokenResponseDto getWaitingInfo(String token){
        Long userId = jwtService.extractUserId(token);

        Long waitingNumber = waitingService.getWaitingNumber(userId);
        boolean isProcessing = waitingNumber == 0;

        return new GetWaitingTokenResponseDto(waitingNumber, isProcessing);
    }

}
