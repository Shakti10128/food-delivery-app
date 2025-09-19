package com.shakti.microservices.common_libs.Dtos.auth;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SigninResponseDto {

    private UserDto user;      // From common-lib
    private String token;      // JWT token
    private String message;    // Optional, e.g., "Login successful"
}