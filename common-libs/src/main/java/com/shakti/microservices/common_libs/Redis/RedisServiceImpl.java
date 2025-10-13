package com.shakti.microservices.common_libs.Redis;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import com.shakti.microservices.common_libs.Exceptions.CustomException;
import com.shakti.microservices.common_libs.Utils.CommonEnv;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RedisServiceImpl implements RedisService{

    private final CommonEnv commonEnv;
    
    private final StringRedisTemplate stringRedisTemplate;

    // Always prefix keys consistently
    private String getRefreshKey(String refreshToken) {
        return "refresh:" + refreshToken;
    }

    private String getAccessKey(String refreshToken) {
        return "accessToken:" + refreshToken;
    }

    
    @Override
    public boolean isRefreshTokenValid(String refreshToken) {
        return stringRedisTemplate.hasKey(getRefreshKey(refreshToken));
    }

    @Override
    public boolean addRefreshToken(String refreshToken,String email) {
        try {
            stringRedisTemplate.opsForValue().set(getRefreshKey(refreshToken), email, commonEnv.getRefreshTokenExpiry());
            return true;
        } catch (Exception e) {
            throw new CustomException(HttpStatus.INTERNAL_SERVER_ERROR.value(),"save operation failed" , "Failed to store access token in Redis");
        }
    }

    @Override
    public String getUserEmailByRefreshToken(String refreshToken) {
        Object value = stringRedisTemplate.opsForValue().get(getRefreshKey(refreshToken));
        return value != null ? value.toString() : null;
    }

    public boolean isAccessTokenValid(String refreshToken) {
        return stringRedisTemplate.hasKey(getAccessKey(refreshToken));
    }

    public boolean addAccessToken(String accessToken,String refreshToken) {
        try {
            stringRedisTemplate.opsForValue().set(getAccessKey(refreshToken),accessToken, commonEnv.getAccessTokenExpiry());
            return true;
        } catch (Exception e) {
            throw new CustomException(HttpStatus.INTERNAL_SERVER_ERROR.value(),"save operation failed" , "Failed to store access token in Redis");
        }
    }

    public boolean inValidateAccessAndRefreshToken(String accessToken,String refreshToken) {
        try {
            System.out.println("Before logout: " + stringRedisTemplate.keys("*"));

            stringRedisTemplate.delete(getAccessKey(refreshToken));
            stringRedisTemplate.delete(getRefreshKey(refreshToken));

            System.out.println("After logout: " + stringRedisTemplate.keys("*"));

            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
