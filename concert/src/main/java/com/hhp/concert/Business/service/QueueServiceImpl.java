package com.hhp.concert.Business.service;

import com.hhp.concert.Business.Domain.ProcessQueue;
import com.hhp.concert.Business.Domain.WaitingQueue;
import com.hhp.concert.Business.ProcessQueueRepository;
import com.hhp.concert.Business.Repository.WaitingRepository;
import com.hhp.concert.util.CustomException;
import org.springframework.beans.factory.annotation.Value;
import com.hhp.concert.util.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;

@Service
@RequiredArgsConstructor
public class QueueServiceImpl implements QueueService {

    @Value("${control.size}")
    private long processingSize;

    private final WaitingRepository waitingRepository;

    private final ProcessQueueRepository processQueueRepository;

    private final JwtService jwtService;

    private final AtomicLong count = new AtomicLong(1);


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

    @Override
    public boolean isProcessing(long userId) {
        return processQueueRepository.existByUserId(userId);
    }

    @Override
    public void removeProcessing(ProcessQueue processQueue) {
        processQueueRepository.delete(processQueue);
    }

    @Override
    @Transactional
    public synchronized void updateQueue() {

        List<ProcessQueue> list = processQueueRepository.findAll();
        long size = list.size();

        // 유효성 체크
        for (ProcessQueue processQueue : list) {
            if (!jwtService.validateToken(processQueue.getToken(), processQueue.getUserId())) {
                removeProcessing(processQueue);
                size--;
            }
        }

        // 대기열의 유저를 처리열로 이동
        while (size < processingSize) {
            moveUserToProcessingQueue();
            size++;
        }

    }


    @Override
    public void moveUserToProcessingQueue(){
        Optional<WaitingQueue> waitingQueue;
        Long size = waitingRepository.count();
        if(size == 0){
            return;
        }
        do {
            waitingQueue = waitingRepository.findById(count.get());
            if(waitingQueue.isEmpty()){
                count.incrementAndGet();
            }
        } while (waitingQueue.isEmpty());

        Long userId = waitingQueue.get().getUserId();
        addProcessingQueue(userId);
    }

    @Override
    public ProcessQueue addProcessingQueue(Long userId){
        Optional<WaitingQueue> waitingQueue = waitingRepository.findByUserId(userId);
        waitingQueue.ifPresent(waitingRepository::delete);
        count.incrementAndGet();
        return processQueueRepository.save(new ProcessQueue(userId, jwtService.createProcessingToken(userId)));
    }

    @Override
    public void removeProcessingByUserId(Long userId) {
        processQueueRepository.deleteByUserId(userId);
    }


}
