package com.hhp.concert.application;

import com.hhp.concert.Business.Domain.User;
import com.hhp.concert.Business.Domain.WaitingQueue;
import com.hhp.concert.Business.UserService;
import com.hhp.concert.Business.WaitingService;
import com.hhp.concert.Business.dto.GetTokenResponseDto;
import com.hhp.concert.Business.dto.GetWaitingTokenResponseDto;
import com.hhp.concert.util.CustomException;
import com.hhp.concert.util.ErrorCode;
import com.hhp.concert.util.JwtUtil;
import com.hhp.concert.util.enums.QueueKey;
import com.hhp.concert.util.enums.QueueType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class WaitingFacade {
    private JwtUtil jwtUtil;
    private WaitingService waitingService;
    private UserService userService;


    public GetTokenResponseDto getToken(Long userId){
        Optional<WaitingQueue> waitingQueue = waitingService.findByUserId(userId);
        if(waitingQueue.isPresent()){
            User user = userService.getUser(waitingQueue.get().getUserId()).orElseThrow(
                    () -> new CustomException(ErrorCode.NOT_FOUND_USER_ID)
            );
            return new GetTokenResponseDto(user.getWaitingToken());
        }
        Map<String, Object> data = Map.of(QueueKey.USER_ID.getStr(), userId);
        String token = jwtUtil.generateWaitingToken(QueueType.WAITING.getStr(), data);
        return new GetTokenResponseDto(token);
    }

    public GetWaitingTokenResponseDto getWaitingInfo(String token){
        Long userId = Long.valueOf(jwtUtil.extractData(token, QueueKey.USER_ID.getStr()));

        Long waitingNumber = waitingService.getWaitingNumber(userId);
        boolean isProcessing = waitingNumber == 0;

        return new GetWaitingTokenResponseDto(waitingNumber, isProcessing);
    }

}
