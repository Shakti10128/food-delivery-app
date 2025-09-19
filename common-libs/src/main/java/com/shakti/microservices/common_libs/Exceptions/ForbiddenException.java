package com.shakti.microservices.common_libs.Exceptions;

public class ForbiddenException extends CustomException {
    public ForbiddenException(String message) {
        super(403, "FORBIDDEN", message);
    }
}