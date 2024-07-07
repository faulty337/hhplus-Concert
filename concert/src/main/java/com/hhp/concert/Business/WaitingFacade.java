package com.hhp.concert.Business;

import com.hhp.concert.Business.Domain.WaitingQueue;
import com.hhp.concert.Business.dto.GetTokenResponseDto;
import com.hhp.concert.Business.dto.GetWaitingTokenResponseDto;
import com.hhp.concert.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class WaitingFacade {
    private JwtUtil jwtUtil;
    private WaitingService waitingService;

    public GetTokenResponseDto getToken(Long userId){
        String token = jwtUtil.generateToken(String.valueOf(userId));
        return new GetTokenResponseDto(token);
    }

    public GetWaitingTokenResponseDto getWaitingInfo(String token){
        Long userId = Long.valueOf(jwtUtil.extractSign(token));

        Long waitingNumber = waitingService.getWaitingNumber(userId);
        boolean isProcessing = waitingNumber == 0;

        return new GetWaitingTokenResponseDto(waitingNumber, isProcessing);
    }

}
