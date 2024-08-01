package com.hhp.concert.Business.service;

import com.hhp.concert.Business.Domain.ProcessQueue;
import com.hhp.concert.Business.Domain.WaitingQueue;
import com.hhp.concert.Business.Repository.ProcessQueueRepository;
import com.hhp.concert.Business.Repository.RedisRepository;
import com.hhp.concert.Business.Repository.WaitingRepository;
import com.hhp.concert.util.enums.QueueType;
import com.hhp.concert.util.exception.CustomException;
import org.springframework.beans.factory.annotation.Value;
import com.hhp.concert.util.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;

@Service
@RequiredArgsConstructor
public class waitingServiceImpl implements waitingService {

    @Value("${control.size}")
    private long processingSize;

    private final WaitingRepository waitingRepository;

    private final ProcessQueueRepository processQueueRepository;

    private final JwtService jwtService;

    private final RedisRepository redisRepository;

    //최근 처리 대기열 첫번째 인덱스 저장
    private final AtomicLong count = new AtomicLong(1);


    @Override
    public Optional<WaitingQueue> waitingQueueByUserId(Long userId) {
        return waitingRepository.findByUserId(userId);
    }

    public Long getWaitingNumber(Long userId){

        Long waitingNumber = redisRepository.getSortedSetRank(QueueType.WAITING.getStr(), userId.toString());
        if(waitingNumber == null){
            redisRepository.addElementSortedSet(QueueType.WAITING.getStr(), userId.toString(), System.currentTimeMillis());
            waitingNumber = redisRepository.getSortedSetRank(QueueType.WAITING.getStr(), userId.toString());
        }
        return waitingNumber;
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


    //스케줄러 함수
    @Override
    @Transactional
    public synchronized void updateQueue() {

        //처리열 조회
        List<ProcessQueue> list = processQueueRepository.findAll();
        long size = list.size();

        // 처리열 각 토큰 유효성 검사
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

        //waiting이 비었을 때 처리(무한반복 위험)
        Long size = waitingRepository.count();
        if(size == 0){
            return;
        }
        //대기열 삭제 및 처리열 삽입
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
