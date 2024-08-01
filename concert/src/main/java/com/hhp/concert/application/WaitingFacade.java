package com.hhp.concert.application;

import com.hhp.concert.Business.Domain.User;
import com.hhp.concert.Business.service.JwtService;
import com.hhp.concert.Business.service.UserService;
import com.hhp.concert.Business.service.waitingService;
import com.hhp.concert.Business.dto.GetWaitingTokenResponseDto;
import com.hhp.concert.util.exception.CustomException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class WaitingFacade {
    private final JwtService jwtService;
    private final waitingService waitingService;
    private final UserService userService;


    @Transactional
    public GetWaitingTokenResponseDto getWaitingInfo(Long userId){
        //유저 유효성 검사
        User user = userService.getUser(userId);

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
        Long waitingNumber = waitingService.getWaitingNumber(userId);


        return new GetWaitingTokenResponseDto(waitingNumber, false, "");

    }



}
