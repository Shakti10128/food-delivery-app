package com.shakti.microservices.common_libs.Exceptions;

public class BadRequestException extends CustomException {
    public BadRequestException(String message) {
        super(400, "BAD_REQUEST", message);
    }
}
