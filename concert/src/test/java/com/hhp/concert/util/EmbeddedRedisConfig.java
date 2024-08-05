package com.hhp.concert.util;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.data.redis.core.RedisTemplate;
import redis.embedded.RedisServer;

import java.io.IOException;
import java.net.ServerSocket;

@Profile("test")
@Configuration
public class EmbeddedRedisConfig {
    private static final Logger log = LoggerFactory.getLogger(EmbeddedRedisConfig.class);
    private RedisServer redisServer;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    public EmbeddedRedisConfig(@Value("${spring.data.redis.port}") int port) throws IOException {
        log.info("Starting embedded redis server on port {}", port);
        this.redisServer = new RedisServer(port);
    }

    @PostConstruct
    public void startRedis() {
        this.redisServer.start();
    }

    @PreDestroy
    public void stopRedis() {
        log.info("Stopping embedded redis server");
        if (this.redisServer.isActive()) {
            this.redisServer.stop();
            log.info("Embedded redis server stopped");
        } else {
            log.warn("Embedded redis server was not active");
        }
    }

    public void initializeRedis() {
        try {
            // 모든 데이터 삭제
            redisTemplate.getConnectionFactory().getConnection().flushAll();
            log.info("Flushed all Redis data");

            // 예시 초기화 로직
            redisTemplate.opsForValue().set("testKey", "testValue");
            log.info("Initialized Redis with test data");
        } catch (Exception e) {
            log.error("Failed to initialize Redis", e);
        }
    }
}