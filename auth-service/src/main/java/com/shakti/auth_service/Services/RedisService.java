package com.shakti.auth_service.Services;

public interface RedisService {
    boolean isRefreshTokenExpired(String token);

    boolean addRefreshToken(String token, String email);

    public String getUserEmailByRefreshToken(String token);
}
