package com.shakti.auth_service.Services;

public interface RedisService {
    boolean isRefreshTokenValid(String token);

    boolean isAccessTokenValid(String refreshToken);

    boolean addAccessToken(String token,String refreshToken);

    boolean addRefreshToken(String token, String email);

    public String getUserEmailByRefreshToken(String token);
}
