package com.shakti.auth_service.Services;

import com.shakti.microservices.common_libs.Dtos.auth.SigninRequestDto;
import com.shakti.microservices.common_libs.Dtos.auth.SigninResponseDto;
import com.shakti.microservices.common_libs.Dtos.auth.SignupRequestDto;
import com.shakti.microservices.common_libs.Dtos.auth.SignupResponseDto;
import com.shakti.microservices.common_libs.Dtos.auth.UserDto;

import jakarta.servlet.http.HttpServletRequest;

public interface AuthService {
    SignupResponseDto signUp(SignupRequestDto signupRequestDto);

    SigninResponseDto signIn(SigninRequestDto signinRequestDto);

    UserDto getLoggedInUser(HttpServletRequest request);
}
