package com.hhp.concert.interfaces.Redis;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.concurrent.TimeUnit;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface RedisLock {
    String key();

    long waitTime() default 1000; // 기본 대기 시간 (밀리초)

    long leaseTime() default 10;

    TimeUnit timeUnit() default TimeUnit.SECONDS;
}