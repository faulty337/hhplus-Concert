package com.hhp.concert.Business.service;

import com.hhp.concert.Business.Domain.WaitingQueue;
import com.hhp.concert.Business.Repository.WaitingRepository;
import com.hhp.concert.util.CustomException;
import com.hhp.concert.util.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class QueueServiceImpl implements QueueService {

    private final WaitingRepository waitingRepository;

    @Override
    public Optional<WaitingQueue> waitingQueueByUserId(Long userId) {
        return waitingRepository.findByUserId(userId);
    }

    public Long getWaitingNumber(Long userId){
        WaitingQueue user = waitingQueueByUserId(userId).orElseThrow(
                () -> new CustomException(ErrorCode.NOT_FOUND_USER_ID)
        );;
        Optional<WaitingQueue> first = waitingRepository.getFirst();

        Long firstNumber = user.getId();

        return first.map(waitingQueue -> firstNumber - waitingQueue.getId()).orElse(0L);
    }

    @Override
    public WaitingQueue addWaiting(WaitingQueue waitingQueue) {
        return waitingRepository.save(waitingQueue);
    }


}
