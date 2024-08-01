package com.hhp.concert.Business.service;

import com.hhp.concert.Business.Domain.User;
import com.hhp.concert.Business.Repository.RedisRepository;
import com.hhp.concert.Business.Repository.UserRepository;
import com.hhp.concert.util.enums.QueueType;
import com.hhp.concert.util.exception.CustomException;
import com.hhp.concert.util.exception.ErrorCode;
import org.springframework.beans.factory.annotation.Value;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class waitingServiceImpl implements waitingService {

    @Value("${control.size}")
    private long processingSize;

    private final JwtService jwtService;

    private final RedisRepository redisRepository;

    private final UserRepository userRepository;


    public Long getWaitingNumber(Long userId){

        Long waitingNumber = redisRepository.getSortedSetRank(QueueType.WAITING.getStr(), userId.toString());
        if(waitingNumber == null){
            redisRepository.addElementSortedSet(QueueType.WAITING.getStr(), userId.toString(), System.currentTimeMillis());
            waitingNumber = redisRepository.getSortedSetRank(QueueType.WAITING.getStr(), userId.toString());
        }
        return waitingNumber;
    }

    //스케줄러 함수
    @Override
    @Transactional
    public synchronized void updateQueue() {

        //처리열 조회
        long size = redisRepository.getSortedSetSize(QueueType.PROCESSING.getStr());

        // 처리열 각 토큰 유효성 검사
        String token = redisRepository.getFirstElement(QueueType.PROCESSING.getStr());
        while(token != null){
            if(!jwtService.isExpiredToken(token)){
                redisRepository.removeElementSortedSet(QueueType.PROCESSING.getStr(), token);
                size--;
            }else{
                break;
            }
            token = redisRepository.getFirstElement(QueueType.PROCESSING.getStr());
        };

        // 대기열의 유저를 처리열로 이동
        while (size < processingSize) {
            moveToProcessingQueue();
            size++;
        }

    }


    @Override
    public void moveToProcessingQueue(){
        String token = redisRepository.getFirstElement(QueueType.WAITING.getStr());
        if(token == null){
            return;
        }

        //대기열 삭제 및 처리열 삽입
        redisRepository.removeElementSortedSet(QueueType.WAITING.getStr(), token);
        redisRepository.addElementSortedSet(QueueType.PROCESSING.getStr(), token, System.currentTimeMillis());
    }

    public void moveUserToProcessingQueue(Long userId){
        User user = userRepository.findById(userId).orElseThrow(
                () -> new CustomException(ErrorCode.NOT_FOUND_USER_ID)
        );
        String token = user.getToken();
        if(!redisRepository.isElementInSortedSet(QueueType.PROCESSING.getStr(), token)){
            return;
        }
        String waitingFirst = redisRepository.getFirstElement(QueueType.WAITING.getStr());
        if(waitingFirst == null){
            return;
        }
        redisRepository.removeElementSortedSet(QueueType.PROCESSING.getStr(), token);
        redisRepository.addElementSortedSet(QueueType.PROCESSING.getStr(), waitingFirst, System.currentTimeMillis());
    }


}
