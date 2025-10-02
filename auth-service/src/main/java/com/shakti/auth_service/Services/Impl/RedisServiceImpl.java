package com.shakti.auth_service.Services.Impl;

import java.time.Duration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import com.shakti.auth_service.Services.RedisService;

@Service
public class RedisServiceImpl implements RedisService{

    @Value("${refresh-token-expiry}")
    private int refreshTokenExpiry;
    
    private final RedisTemplate<String, Object> redisTemplate;

    public RedisServiceImpl(RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @Override
    public boolean isRefreshTokenExpired(String token) {
        String key = "refresh:" + token;
        return redisTemplate.hasKey(key);
    }

    @Override
    public boolean addRefreshToken(String token,String email) {
        String key = "refresh:" + token;
        try {
            redisTemplate.opsForValue().set(key, email, Duration.ofDays(refreshTokenExpiry));
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public String getUserEmailByRefreshToken(String token) {
        String key = "refresh:" + token;
        Object value = redisTemplate.opsForValue().get(key);
        return value != null ? value.toString() : null;
    }
}
