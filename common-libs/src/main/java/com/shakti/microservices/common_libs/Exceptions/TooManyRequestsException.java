package com.shakti.microservices.common_libs.Exceptions;

public class TooManyRequestsException extends CustomException {
    public TooManyRequestsException(String message) {
        super(429, "TOO_MANY_REQUESTS", message);
    }
}