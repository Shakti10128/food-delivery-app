package com.shakti.microservices.common_libs.Exceptions;

public class UnauthorizedException extends CustomException {
    public UnauthorizedException(String message) {
        super(401, "UNAUTHORIZED", message);
    }
}