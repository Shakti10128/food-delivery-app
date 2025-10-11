package com.shakti.microservices.common_libs.Redis;

public interface RedisService {
    boolean isRefreshTokenValid(String token);

    boolean isAccessTokenValid(String refreshToken);

    boolean addAccessToken(String token,String refreshToken);

    boolean addRefreshToken(String token, String email);

    public String getUserEmailByRefreshToken(String token);

    boolean inValidateAccessAndRefreshToken(String accessToken,String refreshToken);
}
