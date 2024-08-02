package com.hhp.concert.util;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import redis.embedded.RedisServer;

import java.io.IOException;
import java.net.ServerSocket;

@Profile("test")
@Configuration
public class EmbeddedRedisConfig {
    private RedisServer redisServer;

    public EmbeddedRedisConfig(@Value("${spring.redis.port}") int port) throws IOException {
        if (port == 0) {
            port = findAvailablePort();
        }
        this.redisServer = new RedisServer(port);
    }

    @PostConstruct
    public void startRedis() {
        this.redisServer.start();
    }

    @PreDestroy
    public void stopRedis() {
        this.redisServer.stop();
    }

    private int findAvailablePort() {
        try (ServerSocket socket = new ServerSocket(0)) {
            return socket.getLocalPort();
        } catch (IOException e) {
            throw new RuntimeException("No available port found", e);
        }
    }
}