package com.hhp.concert.application;

import com.hhp.concert.Business.Domain.User;
import com.hhp.concert.Business.Domain.WaitingQueue;
import com.hhp.concert.Business.service.JwtService;
import com.hhp.concert.Business.service.UserService;
import com.hhp.concert.Business.service.QueueService;
import com.hhp.concert.Business.dto.GetWaitingTokenResponseDto;
import com.hhp.concert.util.exception.CustomException;
import com.hhp.concert.util.exception.ErrorCode;
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
    public GetWaitingTokenResponseDto getWaitingInfo(Long userId){
        //유저 유효성 검사
        User user = userService.getUser(userId).orElseThrow(
                () -> new CustomException(ErrorCode.NOT_FOUND_USER_ID)
        );

        //process 토큰 확인
        String token = user.getToken();
        try{
            if(jwtService.isProcessingToken(token)){
                return new GetWaitingTokenResponseDto(0, true, token);
            }
        }catch (CustomException ignored){

        }

        Optional<WaitingQueue> waitingQueue = queueService.waitingQueueByUserId(userId);

        //대기열 X 시 대기열 삽입
        if(waitingQueue.isEmpty()){
            queueService.addWaiting(new WaitingQueue(userId));
        }

        //번호 조회
        Long waitingNumber = queueService.getWaitingNumber(user.getId());


        return new GetWaitingTokenResponseDto(waitingNumber, false, "");

    }



}
