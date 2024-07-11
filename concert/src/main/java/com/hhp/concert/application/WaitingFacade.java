package com.hhp.concert.application;

import com.hhp.concert.Business.Domain.User;
import com.hhp.concert.Business.Domain.WaitingQueue;
import com.hhp.concert.Business.service.JwtService;
import com.hhp.concert.Business.service.UserService;
import com.hhp.concert.Business.service.QueueService;
import com.hhp.concert.Business.dto.GetTokenResponseDto;
import com.hhp.concert.Business.dto.GetWaitingTokenResponseDto;
import com.hhp.concert.util.CustomException;
import com.hhp.concert.util.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class WaitingFacade {
    private final JwtService jwtService;
    private final QueueService queueService;
    private final UserService userService;


    @Transactional
    public GetTokenResponseDto getToken(Long userId){
        Optional<WaitingQueue> waitingQueue = queueService.waitingQueueByUserId(userId);
        if(waitingQueue.isPresent()){
            User user = userService.getUser(waitingQueue.get().getUserId()).orElseThrow(
                    () -> new CustomException(ErrorCode.NOT_FOUND_USER_ID)
            );
            return new GetTokenResponseDto(user.getToken());
        }

        String token = jwtService.createProcessingToken(userId);


        userService.updateToken(userId, token);
        queueService.addWaiting(new WaitingQueue(userId));

        return new GetTokenResponseDto(token);
    }

    @Transactional
    public GetWaitingTokenResponseDto getWaitingInfo(Long userId){
        User user = userService.getUser(userId).orElseThrow(
                () -> new CustomException(ErrorCode.NOT_FOUND_USER_ID)
        );
        Optional<WaitingQueue> waitingQueue = queueService.waitingQueueByUserId(userId);
        Long waitingNumber;
        if(waitingQueue.isEmpty()){
            queueService.addWaiting(new WaitingQueue(userId));
        }
        waitingNumber = queueService.getWaitingNumber(user.getId());

        boolean isProcessing = waitingNumber == 0;

        return new GetWaitingTokenResponseDto(waitingNumber, isProcessing);
    }


}
