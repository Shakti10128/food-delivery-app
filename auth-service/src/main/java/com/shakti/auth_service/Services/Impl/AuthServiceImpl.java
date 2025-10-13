package com.shakti.auth_service.Services.Impl;

import java.util.Map;
import java.util.UUID;

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
import com.shakti.microservices.common_libs.Redis.RedisService;
import com.shakti.microservices.common_libs.Utils.CommonEnv;
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
    private final RedisService redisService;
    private final JwtUtils jwtUtils;
    private final CommonEnv commonEnv;



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
            String accessToken  = jwtUtils.generateToken(loggedUser.getEmail(), claims);
            String refreshToken = UUID.randomUUID().toString();

            Cookie refreshCookie = new Cookie("refreshToken", refreshToken);
            refreshCookie.setHttpOnly(true);
            refreshCookie.setSecure(true);
            refreshCookie.setPath("/");
            // getting the refreshTokenExpiry in MS convert it into S
            // coz cookie expect second not miliseconds
            refreshCookie.setMaxAge((int)commonEnv.getRefreshTokenExpiry().toSeconds());
            
            response.addCookie(refreshCookie);

            redisService.addRefreshToken(refreshToken, loggedUser.getEmail());
            redisService.addAccessToken(accessToken, refreshToken);

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

            for (Cookie cookie : cookies) {
                if ("refreshToken".equals(cookie.getName())) {
                    return cookie.getValue();
                }
            }

            return null;
        } catch (Exception e) {
            System.out.println("Error while extracting the refreshToken from cookie.");
            return null;
        }
    }

    @Override
    public void signOut(HttpServletRequest request, HttpServletResponse response) {
        try {
            // Extract access token safely
            String authorizationHeader = request.getHeader("Authorization");
            String accessToken = null;

            if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
                accessToken = authorizationHeader.substring(7);
            }

            // Extract refresh token from cookie
            String refreshToken = getRefreshTokenFromCookie(request);

            // Remove the refresh cookie
            Cookie refreshCookie = new Cookie("refreshToken", null);
            refreshCookie.setHttpOnly(true);
            refreshCookie.setSecure(true);
            refreshCookie.setPath("/"); // match the path you set when issuing
            refreshCookie.setMaxAge(0); // deletes the cookie
            response.addCookie(refreshCookie);

            // Delete tokens from Redis (handle nulls inside method)
            redisService.inValidateAccessAndRefreshToken(accessToken, refreshToken);

        } catch (Exception e) {
            throw new CustomException(
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                "Please try again",
                "Failed to sign out user"
            );
        }
    }

    
    @Override
    public UserDto getLoggedInUser(HttpServletRequest request) {
        try {
            
            String refreshToken = getRefreshTokenFromCookie(request);

            // check the refreshToken is exist or not in cookie OR it's expired or not
            if(refreshToken == null || !redisService.isRefreshTokenValid(refreshToken)) {
               throw new UnauthorizedException("Please login."); 
            }

            String email = redisService.getUserEmailByRefreshToken(refreshToken);
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
            if(refreshToken == null || !redisService.isRefreshTokenValid(refreshToken)) {
                throw new UnauthorizedException("Please login.");
            }

            // check either there is already accessToken active for the refreshToken
            if(redisService.isAccessTokenValid(refreshToken)) {
                throw new ConflictException("Access token is still valid. Please use the existing one.");
            }
            
            // access token is expired, generate a new token
            else{
                String email = redisService.getUserEmailByRefreshToken(refreshToken);
                User loggedUser = authRepository.findByEmail(email).get();
                Map<String,Object> claims = Map.of(
                "Role",loggedUser.getRole()
                );

                // generating access token and refresh token
                String accessToken  = jwtUtils.generateToken(loggedUser.getEmail(), claims);
                redisService.addAccessToken(accessToken, refreshToken);

                return accessToken;
            }

        } catch (CustomException e) {
            throw e;
        }
    }
}