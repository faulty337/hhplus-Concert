package com.hhp.concert.application;

import com.hhp.concert.Business.Domain.User;
import com.hhp.concert.Business.service.JwtService;
import com.hhp.concert.Business.service.UserService;
import com.hhp.concert.Business.service.WaitingService;
import com.hhp.concert.Business.dto.GetWaitingTokenResponseDto;
import com.hhp.concert.util.exception.CustomException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class WaitingFacade {
    private final JwtService jwtService;
    private final WaitingService waitingService;
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
        //처리열 비었을 경우 바로 실행
        token = waitingService.enqueueToProcessingQueueIfAvailable(userId);
        if(token != null){
            return new GetWaitingTokenResponseDto(0, true, token);
        }
        //번호 조회 및 대기열 삽입
        Long waitingNumber = waitingService.getWaitingNumber(userId);


        return new GetWaitingTokenResponseDto(waitingNumber, false, "");

    }



}
