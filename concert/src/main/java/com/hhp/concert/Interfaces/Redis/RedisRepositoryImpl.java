package com.hhp.concert.Interfaces.Redis;

import com.hhp.concert.Business.Repository.RedisRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Repository;

import java.util.Set;

@Repository
@RequiredArgsConstructor
public class RedisRepositoryImpl implements RedisRepository {

    private final RedisTemplate<String, String> redisTemplate;

    // 요소 추가
    public void addElementSortedSet(String key, String value, double score) {
        ZSetOperations<String, String> zSetOps = redisTemplate.opsForZSet();
        zSetOps.add(key, value, score);
    }

    // 현재 순위 조회
    public Long getSortedSetRank(String key, String value) {
        ZSetOperations<String, String> zSetOps = redisTemplate.opsForZSet();
        return zSetOps.rank(key, value);
    }

    // 가장 앞에 있는 요소 가져오기
    public String getFirstElement(String key) {
        ZSetOperations<String, String> zSetOps = redisTemplate.opsForZSet();
        Set<String> range = zSetOps.range(key, 0, 0);
        if (range != null && !range.isEmpty()) {
            return range.iterator().next(); // 첫 번째 요소 반환
        }
        return null;
    }

    @Override
    public void removeElementSortedSet(String str, String token) {
        ZSetOperations<String, String> zSetOps = redisTemplate.opsForZSet();
        zSetOps.remove(str, token);
    }


    // 전체 크기 가져오기
    public long getSortedSetSize(String key) {
        ZSetOperations<String, String> zSetOps = redisTemplate.opsForZSet();
        return zSetOps.zCard(key);
    }

    // 범위 내의 요소 가져오기
    public Set<String> getSortedSetRange(String key, long start, long end) {
        ZSetOperations<String, String> zSetOps = redisTemplate.opsForZSet();
        return zSetOps.range(key, start, end);
    }

    public boolean isElementInSortedSet(String key, String value) {
        ZSetOperations<String, String> zSetOps = redisTemplate.opsForZSet();
        Long rank = zSetOps.rank(key, value);
        return rank != null;
    }
}
