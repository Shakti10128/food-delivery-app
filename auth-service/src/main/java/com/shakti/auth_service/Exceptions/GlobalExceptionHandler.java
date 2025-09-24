package com.shakti.auth_service.Exceptions;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.shakti.microservices.common_libs.Dtos.ErrorResponse;
import com.shakti.microservices.common_libs.Exceptions.CustomException;

import jakarta.servlet.http.HttpServletRequest;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(CustomException.class)
    public ResponseEntity<ErrorResponse> customExceptionHandler(CustomException ex,HttpServletRequest rq) {
        ErrorResponse error = ErrorResponse.of(ex.getStatus(), ex.getError(), ex.getMessage(), rq.getRequestURI());

        return new ResponseEntity<ErrorResponse>(error, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> validationExceptionHandler(
            MethodArgumentNotValidException ex, HttpServletRequest rq) {

        Map<String, String> errors = new HashMap<>();

        // Loop through field errors
        ex.getBindingResult().getFieldErrors().forEach(error -> {
            errors.put(error.getField(), error.getDefaultMessage());
        });

        errors.put("success", "false");
        errors.put("path", rq.getRequestURI());
        errors.put("message", "Required fields are missing");


        return new ResponseEntity<Map<String,String>>(errors, HttpStatus.UNAUTHORIZED);
    }

}
