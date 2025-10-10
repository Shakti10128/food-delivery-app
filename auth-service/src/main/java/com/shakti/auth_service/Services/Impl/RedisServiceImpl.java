package com.shakti.auth_service.Services.Impl;

import java.time.Duration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.shakti.auth_service.Services.RedisService;
import com.shakti.microservices.common_libs.Exceptions.CustomException;

@Service
public class RedisServiceImpl implements RedisService{

    @Value("${refresh-token-expiry}")
    private int refreshTokenExpiry;

    @Value("${access-token-expiry}")
    private int accessTokenExpiry;
    
    private final RedisTemplate<String, Object> redisTemplate;

    public RedisServiceImpl(RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @Override
    public boolean isRefreshTokenValid(String token) {
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
            throw new CustomException(HttpStatus.INTERNAL_SERVER_ERROR.value(),"save operation failed" , "Failed to store access token in Redis");
        }
    }

    @Override
    public String getUserEmailByRefreshToken(String token) {
        String key = "refresh:" + token;
        Object value = redisTemplate.opsForValue().get(key);
        return value != null ? value.toString() : null;
    }

    public boolean isAccessTokenValid(String refreshToken) {
        String key = "accessToken:" + refreshToken;
        return redisTemplate.hasKey(key);
    }

    public boolean addAccessToken(String token,String refreshToken) {
        String key = "accessToken:" + refreshToken;
        try {
            redisTemplate.opsForValue().set(key,token, Duration.ofDays(accessTokenExpiry));
            return true;
        } catch (Exception e) {
            throw new CustomException(HttpStatus.INTERNAL_SERVER_ERROR.value(),"save operation failed" , "Failed to store access token in Redis");
        }
    }
}
