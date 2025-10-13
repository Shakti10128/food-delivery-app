package com.shakti.auth_service.Controller;

import java.util.Map;

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
import com.shakti.microservices.common_libs.Dtos.auth.UserDto;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;


@RestController
@AllArgsConstructor
@RequestMapping("auth")
public class AuthController {
    private final AuthService authService;
    

    @PostMapping("/signup")
    public ResponseEntity<SignupResponseDto> signUpHandler(@Valid @RequestBody SignupRequestDto signupRequestDto) {
        SignupResponseDto response = authService.signUp(signupRequestDto);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PostMapping("/signin")
    public ResponseEntity<SigninResponseDto> signinHandler(@Valid @RequestBody SigninRequestDto signinRequestDto, HttpServletResponse response) {
        System.out.println("entered into Controller");
        SigninResponseDto resp = authService.signIn(signinRequestDto,response);

        return new ResponseEntity<SigninResponseDto>(resp, HttpStatus.OK);
    }

    @GetMapping("/get-logged-user")
    public ResponseEntity<UserDto> getLoggedInUserHandler(HttpServletRequest request) {
        UserDto userDto = authService.getLoggedInUser(request);
        return new ResponseEntity<UserDto>(userDto, HttpStatus.OK);
    }

    @GetMapping("/accessToken/refresh") 
    public ResponseEntity<Map<String,Object>> getNewAccessTokenHandler(HttpServletRequest request) {
        String accessToken = authService.getAccessToken(request);

        Map<String,Object> result = Map.of(
            "success",true,
            "message","access token issued successfully",
            "token",accessToken
        );

        return new ResponseEntity<Map<String,Object>>(result, HttpStatus.OK);
    }

    @GetMapping("/signout")
    public ResponseEntity<Map<String,Object>> signoutHandler(HttpServletRequest request, HttpServletResponse response) {

        authService.signOut(request, response);

        Map<String,Object> result = Map.of(
            "success",true,
            "message","signout successfully"
        );

        return new ResponseEntity<Map<String,Object>>(result, HttpStatus.OK);
    }
    
}
