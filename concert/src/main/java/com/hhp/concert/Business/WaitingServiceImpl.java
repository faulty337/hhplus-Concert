package com.hhp.concert.Business;

import com.hhp.concert.Business.Domain.WaitingQueue;
import com.hhp.concert.Business.Repository.WaitingRepository;
import com.hhp.concert.Business.dto.GetWaitingTokenResponseDto;
import com.hhp.concert.util.CustomException;
import com.hhp.concert.util.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class WaitingServiceImpl implements WaitingService{

    private WaitingRepository waitingRepository;

    @Override
    public WaitingQueue findByUserId(Long userId) {
        return waitingRepository.findByUserId(userId).orElseThrow(
                () -> new CustomException(ErrorCode.NOT_FOUND_USER_ID)
        );
    }

    public Long getWaitingNumber(Long userId){
        WaitingQueue user = findByUserId(userId);
        Optional<WaitingQueue> first = waitingRepository.getFirst();
        Long firstNumber = user.getId();
        if(first.isPresent()){
            return firstNumber -= first.get().getId();
        }else{
            return firstNumber;
        }
    }
}
