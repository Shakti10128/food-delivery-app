package com.shakti.microservices.common_libs.Utils;

import java.time.Duration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;


@Service
public class CommonEnv {
    
    @Value("${refresh-token-expiry}")
    private Duration refreshTokenExpiry;

    @Value("${access-token-expiry}")
    private Duration accessTokenExpiry;

    @Value("${jwt-secret-key}")
    private String jwtSecret;


    public Duration getRefreshTokenExpiry(){
        return this.refreshTokenExpiry;
    }

    public Duration getAccessTokenExpiry(){
        return this.accessTokenExpiry;
    }

    public String getJwtSecret(){
        return this.jwtSecret;
    }
}
