package com.shakti.microservices.common_libs.Dtos.auth;

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

    // Optional fields if you want
    private String phoneNumber;
    private String address;
}