package com.shakti.auth_service.Controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.shakti.auth_service.Services.AuthService;
import com.shakti.microservices.common_libs.Dtos.auth.SigninRequestDto;
import com.shakti.microservices.common_libs.Dtos.auth.SigninResponseDto;
import com.shakti.microservices.common_libs.Dtos.auth.SignupRequestDto;
import com.shakti.microservices.common_libs.Dtos.auth.SignupResponseDto;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;

@RestController
@AllArgsConstructor
@RequestMapping("users/auth")
public class AuthController {
    private final AuthService authService;

    @PostMapping("/signup")
    public ResponseEntity<SignupResponseDto> signUpHandler(@Valid @RequestBody SignupRequestDto signupRequestDto) {
        SignupResponseDto response = authService.signUp(signupRequestDto);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PostMapping("/signin")
    public ResponseEntity<SigninResponseDto> signinHandler(@Valid @RequestBody SigninRequestDto signinRequestDto) {
        SigninResponseDto response = authService.signIn(signinRequestDto);

        return new ResponseEntity<SigninResponseDto>(response, HttpStatus.OK);
    }
}
