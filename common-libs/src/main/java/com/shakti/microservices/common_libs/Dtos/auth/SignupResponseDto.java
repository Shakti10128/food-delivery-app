package com.shakti.microservices.common_libs.Dtos.auth;

import com.shakti.microservices.common_libs.Enums.Role;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SignupResponseDto {

    private Long userId;
    private String username;
    private String email;
    private Role role;  // From common-lib Role enum
    private boolean active;
    private String message;  // Optional, e.g., "Signup successful"
}