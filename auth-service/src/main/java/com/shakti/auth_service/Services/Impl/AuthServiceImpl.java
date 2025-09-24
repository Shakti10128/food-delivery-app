package com.shakti.auth_service.Services.Impl;

import java.util.Map;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.shakti.auth_service.Entity.OutboxEvent;
import com.shakti.auth_service.Entity.User;
import com.shakti.auth_service.Repository.AuthRepository;
import com.shakti.auth_service.Repository.OutboxRepository;
import com.shakti.auth_service.Services.AuthService;
import com.shakti.microservices.common_libs.Dtos.auth.SigninRequestDto;
import com.shakti.microservices.common_libs.Dtos.auth.SigninResponseDto;
import com.shakti.microservices.common_libs.Dtos.auth.SignupRequestDto;
import com.shakti.microservices.common_libs.Dtos.auth.SignupResponseDto;
import com.shakti.microservices.common_libs.Dtos.auth.UserDto;
import com.shakti.microservices.common_libs.Enums.EventType;
import com.shakti.microservices.common_libs.Exceptions.CustomException;
import com.shakti.microservices.common_libs.Exceptions.UnauthorizedException;
import com.shakti.microservices.common_libs.Utils.JwtUtils;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService{
    private final AuthRepository authRepository;
    private final PasswordEncoder passwordEncoder;
    private final OutboxRepository outboxRepository;
    private final ObjectMapper objectMapper;
    private final RedisServiceImpl redisServiceImpl;

    @Value("${jwt-secret-key}")
    private String SECRET_KEY;

    @Value("${refresh-token-expiry}")
    private static int refreshTokenExpiry;


    @Override
    @Transactional(rollbackOn = Exception.class) // Rollback for all exceptions
    public SignupResponseDto signUp(SignupRequestDto signupRequestDto) {
        try { 
            // check user already exist or not
            if (authRepository.existsByEmail(signupRequestDto.getEmail())) {
                throw new CustomException(HttpStatus.CONFLICT.value(), "Duplicate entry", "Email already exist");
            }

            User user = User.builder()
                    .username(signupRequestDto.getUsername())
                    .email(signupRequestDto.getEmail())
                    .password(passwordEncoder.encode(signupRequestDto.getPassword()))
                    .role(signupRequestDto.getRole())
                    .Active(true)
                    .build();

            authRepository.save(user);

            SignupResponseDto response = SignupResponseDto.builder()
                    .userId(user.getId())
                    .email(user.getEmail())
                    .role(user.getRole())
                    .username(user.getUsername())
                    .active(true)
                    .message("Signup successfully")
                    .build();

            // Save Outbox event (in same transaction)
            OutboxEvent event = new OutboxEvent();
            event.setEventType(EventType.USER_SIGNUP);
            try {
                event.setPayload(objectMapper.writeValueAsString(response)); // Injected ObjectMapper
            } catch (JsonProcessingException e) {
                throw new CustomException(HttpStatus.EXPECTATION_FAILED.value(), "Serialization error", "Failed to serialize signup response");
            }
            outboxRepository.save(event);

            return response;
        } catch (CustomException e) {
            throw e;
        }
    }

    
    @Override
    @Transactional(rollbackOn = Exception.class)
    public SigninResponseDto signIn(SigninRequestDto signinRequestDto,HttpServletResponse response) {
        try {
            // check the user is registered or not
            System.out.println("checking user exist or not");
            User loggedUser = authRepository.findByEmail(signinRequestDto.getEmail())
            .orElseThrow(() -> new UnauthorizedException("Please use correct email & password"));

            // check password is correct or not
            System.out.println("user exist, matching password");
            if(!passwordEncoder.matches(signinRequestDto.getPassword(), loggedUser.getPassword())) {
                throw new UnauthorizedException("Please use correct email & password");
            }

            System.out.println("password matche");
            Map<String,Object> claims = Map.of(
                "Role",loggedUser.getRole()
            );

            // generating access token and refresh token
            String accessToken  = JwtUtils.generateToken(SECRET_KEY,loggedUser.getEmail(), claims);
            String refreshToken = UUID.randomUUID().toString();

            Cookie refreshCookie = new Cookie(refreshToken, refreshToken);
            refreshCookie.setHttpOnly(true);
            refreshCookie.setSecure(true);
            refreshCookie.setPath("/auth/refresh");
            refreshCookie.setMaxAge(refreshTokenExpiry);

            response.addCookie(refreshCookie);
            redisServiceImpl.addRefreshToken(refreshToken, loggedUser.getEmail());

            System.out.println("refreshToken: " + refreshToken);
            return SigninResponseDto.builder()
                                    .user(UserDto.builder()
                                                .id(loggedUser.getId())
                                                .username(loggedUser.getUsername())
                                                .email(loggedUser.getEmail())
                                                .active(loggedUser.getActive())
                                                .role(loggedUser.getRole().toString())
                                                .build()
                                    )
                                    .token(accessToken)
                                    .message("Logged in successfully")
                                    .build();
        } catch (UnauthorizedException e) {
            throw e;
        }
    }
    
    @Override
    public UserDto getLoggedInUser(HttpServletRequest request) {
        return null;
    }
}