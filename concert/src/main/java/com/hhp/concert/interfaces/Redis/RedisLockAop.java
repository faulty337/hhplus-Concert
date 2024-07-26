package com.hhp.concert.interfaces.Redis;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Aspect
@Component
@Slf4j
@RequiredArgsConstructor
public class RedisLockAop {
    private final StringRedisTemplate redisTemplate;
    private final RedisCallTransaction redisCallTransaction;

    @Around("@annotation(redisLock)")
    public Object lock(final ProceedingJoinPoint joinPoint, RedisLock redisLock) throws Throwable {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();

        String key = this.createKey(signature.getParameterNames(), joinPoint.getArgs(), redisLock.key());
        String lockValue = UUID.randomUUID().toString();

        long waitTime = redisLock.waitTime();
        long leaseTime = redisLock.leaseTime();
        TimeUnit timeUnit = redisLock.timeUnit();

        boolean isLocked = false;
        try {
            while (waitTime > 0) {
                isLocked = redisTemplate.opsForValue().setIfAbsent(key, lockValue, leaseTime, timeUnit);
                if (isLocked) {
                    break;
                }
                waitTime -= 100;
                Thread.sleep(100);
            }

            if (!isLocked) {
                throw new IllegalStateException("Unable to acquire lock for key: " + key);
            }

            log.info("Acquired Redis lock with key: {}", key);
            return joinPoint.proceed();
        } finally {
            this.releaseLock(key, lockValue);
        }
    }

    private String createKey(String[] parameterNames, Object[] args, String key) {
        String resultKey = key;
        for (int i = 0; i < parameterNames.length; i++) {
            if (parameterNames[i].equals(key)) {
                resultKey += args[i];
                break;
            }
        }
        return resultKey;
    }

    private void releaseLock(String key, String value) {
        String lockValue = redisTemplate.opsForValue().get(key);
        if (value.equals(lockValue)) {
            redisTemplate.delete(key);
        }
    }
}