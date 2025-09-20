package com.shakti.auth_service.Services.Impl;

import org.springframework.stereotype.Service;

import com.shakti.auth_service.Services.AuthService;
import com.shakti.microservices.common_libs.Dtos.auth.SigninResponseDto;
import com.shakti.microservices.common_libs.Dtos.auth.SignupRequestDto;
import com.shakti.microservices.common_libs.Dtos.auth.SignupResponseDto;
import com.shakti.microservices.common_libs.Dtos.auth.UserDto;
import jakarta.servlet.http.HttpServletRequest;

@Service
public class AuthServiceImpl implements AuthService{

    @Override
    public SignupResponseDto signUp(SignupRequestDto signupRequestDto) {
        return null;
    }
    
    @Override
    public SigninResponseDto singIn(SignupRequestDto signupRequestDto) {
        return null;
    }
    
    @Override
    public UserDto getLoggedInUser(HttpServletRequest request) {
        return null;
    }
}