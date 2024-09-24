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
import java.util.Objects;

@Profile("test")
@Configuration
public class EmbeddedRedisConfig {
    private static final Logger log = LoggerFactory.getLogger(EmbeddedRedisConfig.class);
    private RedisServer redisServer;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    public EmbeddedRedisConfig(@Value("${spring.data.redis.port}") int port) throws IOException {
        if (isPortInUse(port)) {
            log.warn("Port {} is already in use. Skipping embedded Redis server startup.", port);
        } else {
            log.info("Starting embedded redis server on port {}", port);
            this.redisServer = new RedisServer(port);
        }
    }

    @PostConstruct
    public void startRedis() {
        if (redisServer != null) {
            redisServer.start();
            log.info("Embedded redis server started");
        }
    }

    @PreDestroy
    public void stopRedis() {
        if (redisServer != null && redisServer.isActive()) {
            redisServer.stop();
            log.info("Embedded redis server stopped");
        }
    }

    public void initializeRedis() {
        try {
            redisTemplate.getConnectionFactory().getConnection().flushAll();
            log.info("Flushed all Redis data");

            redisTemplate.opsForValue().set("testKey", "testValue");
            log.info("Initialized Redis with test data");
        } catch (Exception e) {
            log.error("Failed to initialize Redis", e);
        }
    }

    private boolean isPortInUse(int port) {
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            serverSocket.setReuseAddress(true);
            return false;
        } catch (IOException e) {
            return true;
        }
    }
}
