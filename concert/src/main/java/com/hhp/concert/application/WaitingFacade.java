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

import java.util.Objects;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class WaitingFacade {
    private final JwtService jwtService;
    private final QueueService queueService;
    private final UserService userService;


//    public GetTokenResponseDto getToken(Long userId){
//        // 유효성 검사
//        User user = userService.getUser(userId).orElseThrow(
//                () -> new CustomException(ErrorCode.NOT_FOUND_USER_ID)
//        );
//
//        //토큰 확인 및 예외 처리
//        if(user.getToken().isEmpty()){
//            throw new CustomException(ErrorCode.NOT_AUTHORITY);
//        }
//
//        //
//        try {
//            if(Objects.equals(userId, jwtService.extractUserId(user.getToken()))){
//                return new GetTokenResponseDto(user.getToken());
//            }
//        }catch (Exception e){
//            throw new CustomException(ErrorCode.NOT_AUTHORITY);
//        }
//        throw new CustomException(ErrorCode.NOT_AUTHORITY);
//    }

    @Transactional
    public GetWaitingTokenResponseDto getWaitingInfo(Long userId){
        //유저 유효성 검사
        User user = userService.getUser(userId).orElseThrow(
                () -> new CustomException(ErrorCode.NOT_FOUND_USER_ID)
        );

        //process 토큰 확인
        String token = user.getToken();
        if(jwtService.isProcessingToken(token)){
            return new GetWaitingTokenResponseDto(0, true, token);
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
