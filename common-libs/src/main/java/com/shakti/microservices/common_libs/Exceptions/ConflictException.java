package com.shakti.microservices.common_libs.Exceptions;

public class ConflictException extends CustomException {
    public ConflictException(String message) {
        super(409, "CONFLICT", message);
    }
}
