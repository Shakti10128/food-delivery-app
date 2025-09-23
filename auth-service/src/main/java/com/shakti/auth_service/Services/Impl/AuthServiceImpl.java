package com.shakti.auth_service.Services.Impl;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.shakti.auth_service.Entity.User;
import com.shakti.auth_service.Repository.AuthRepository;
import com.shakti.auth_service.Services.AuthService;
import com.shakti.microservices.common_libs.Dtos.auth.SigninRequestDto;
import com.shakti.microservices.common_libs.Dtos.auth.SigninResponseDto;
import com.shakti.microservices.common_libs.Dtos.auth.SignupRequestDto;
import com.shakti.microservices.common_libs.Dtos.auth.SignupResponseDto;
import com.shakti.microservices.common_libs.Dtos.auth.UserDto;
import com.shakti.microservices.common_libs.Exceptions.CustomException;
import com.shakti.microservices.common_libs.Exceptions.UnauthorizedException;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService{
    private final AuthRepository authRepository;
    private final PasswordEncoder passwordEncoder;
    private final RabbitTemplate rabbitTemplate;

    @Override
    public SignupResponseDto signUp(SignupRequestDto signupRequestDto) {

        System.out.println("signupRequestDto is: " + signupRequestDto);

        // check user already exist or not
        if(authRepository.existsByEmail(signupRequestDto.getEmail())) {
            throw new CustomException(HttpStatus.CONFLICT.value(), "Duplicate entry", "Email already exist");
        }

        User user = User.builder()
                        .username(signupRequestDto.getUsername())
                        .email(signupRequestDto.getEmail())
                        .password(passwordEncoder.encode(signupRequestDto.getPassword()))
                        .role(signupRequestDto.getRole())
                        .Active(true)
                        .build();
        
        // save the user
        authRepository.save(user);

        
        // creating the response DTO
        SignupResponseDto response = SignupResponseDto.builder()
        .userId(user.getId())
        .email(user.getEmail())
        .role(user.getRole())
        .username(user.getUsername())
        .active(true)
        .message("Signup successfully")
        .build();

        // Trigger the event to create profile
        rabbitTemplate.convertAndSend("create-profile", response);

        return response;
    }
    
    @Override
    public SigninResponseDto signIn(SigninRequestDto signinRequestDto) {
        System.out.println("signinRequestDto is: " + signinRequestDto);
        // check the user is registered or not
        User loggedUser = authRepository.findByEmail(signinRequestDto.getEmail())
        .orElseThrow(() -> new UnauthorizedException("Please use correct email & password"));

        // check password is correct or not
        if(!passwordEncoder.matches(signinRequestDto.getPassword(), loggedUser.getPassword())) {
            throw new UnauthorizedException("Please use correct email & password");
        }

        
        return SigninResponseDto.builder()
                                .user(UserDto.builder()
                                             .id(loggedUser.getId())
                                             .username(loggedUser.getUsername())
                                             .email(loggedUser.getEmail())
                                             .active(loggedUser.getActive())
                                             .role(loggedUser.getRole().toString())
                                             .build()
                                )
                                // hard-coded token will change later to actual token
                                .token("kjasldfjasldfalksdfljalsdjflkadf")
                                .message("Logged in successfully")
                                .build();
    }
    
    @Override
    public UserDto getLoggedInUser(HttpServletRequest request) {
        return null;
    }
}