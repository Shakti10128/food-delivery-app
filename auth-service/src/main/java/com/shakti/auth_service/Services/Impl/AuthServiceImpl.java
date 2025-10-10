package com.shakti.auth_service.Services.Impl;

import java.util.Map;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
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
import com.shakti.microservices.common_libs.Exceptions.ConflictException;
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
    private final RedisServiceImpl redisServiceImpl;

    // @Value("${jwt-secret-key}")
    // private String SECRET_KEY;

    @Value("${refresh-token-expiry}")
    private int refreshTokenExpiry;

    @Value("${access-token-expiry}")
    private int accessTokenExpiry;



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
            User loggedUser = authRepository.findByEmail(signinRequestDto.getEmail())
            .orElseThrow(() -> new UnauthorizedException("Please use correct email & password."));

            // check password is correct or not
            if(!passwordEncoder.matches(signinRequestDto.getPassword(), loggedUser.getPassword())) {
                throw new UnauthorizedException("Please use correct email & password.");
            }

            Map<String,Object> claims = Map.of(
                "Role",loggedUser.getRole()
            );

            // generating access token and refresh token
            String accessToken  = JwtUtils.generateToken(accessTokenExpiry,loggedUser.getEmail(), claims);
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
        } catch (CustomException e) {
            throw e;
        }
    }

    private String getRefreshTokenFromCookie(HttpServletRequest request) {
        try {
            Cookie[] cookies = request.getCookies();
            if(cookies == null) return null;
            
            String refreshToken = null;

            for(int i = 0; i < cookies.length; i++) {
                if(cookies[i].getName().equals("refreshToken")) {
                    // refreshToken found store it & break the loop
                    refreshToken = cookies[i].getValue();
                    break;
                }
            }

            return refreshToken;
        } catch (Exception e) {
            System.out.println("Error while extracting the refreshToken from cookie.");
            return null;
        }
    }
    
    @Override
    public UserDto getLoggedInUser(HttpServletRequest request) {
        try {
            
            String refreshToken = getRefreshTokenFromCookie(request);

            // check the refreshToken is exist or not in cookie OR it's expired or not
            if(refreshToken == null || !redisServiceImpl.isRefreshTokenValid(refreshToken)) {
               throw new UnauthorizedException("Please login."); 
            }

            String email = redisServiceImpl.getUserEmailByRefreshToken(refreshToken);
            User user = authRepository.findByEmail(email).get();

            return UserDto.builder()
                          .id(user.getId())
                          .username(user.getUsername())
                          .email(user.getEmail())
                          .active(user.getActive())
                          .role(user.getRole().toString())
                          .build();


        } catch (CustomException e) {
            throw e;
        }
    }

    @Override
    public String getAccessToken(HttpServletRequest request) {

        try {
            String refreshToken = getRefreshTokenFromCookie(request);
            // check the refreshToken is exist or not in cookie OR it's expired or not
            if(refreshToken == null || !redisServiceImpl.isRefreshTokenValid(refreshToken)) {
                throw new UnauthorizedException("Please login.");
            }

            // check either there is already accessToken active for the refreshToken
            if(redisServiceImpl.isAccessTokenValid(refreshToken)) {
                throw new ConflictException("Access token is still valid. Please use the existing one.");
            }
            
            // access token is expired, generate a new token
            else{
                String email = redisServiceImpl.getUserEmailByRefreshToken(refreshToken);
                User loggedUser = authRepository.findByEmail(email).get();
                Map<String,Object> claims = Map.of(
                "Role",loggedUser.getRole()
                );

                // generating access token and refresh token
                String accessToken  = JwtUtils.generateToken(accessTokenExpiry,loggedUser.getEmail(), claims);
                redisServiceImpl.addAccessToken(accessToken, refreshToken);

                return accessToken;
            }

        } catch (CustomException e) {
            throw e;
        }
    }
}