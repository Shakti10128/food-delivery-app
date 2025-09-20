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
public class SignupRequestDto {

    private String username;
    private String email;
    private String password;
    private Role role;
}