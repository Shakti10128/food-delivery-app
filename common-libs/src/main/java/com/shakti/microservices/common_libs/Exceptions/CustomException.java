package com.shakti.microservices.common_libs.Exceptions;

import lombok.Getter;

@Getter
public class CustomException extends RuntimeException {
    
    private final int status;   // HTTP status code
    private final String error; // short reason (e.g. NOT_FOUND)

    public CustomException(int status, String error, String message) {
        super(message); // sets the exception message
        this.status = status;
        this.error = error;
    }
}
