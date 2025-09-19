package com.shakti.microservices.common_libs.Exceptions;

public class ResourceNotFoundException extends CustomException {
    public ResourceNotFoundException(String message) {
        super(404, "NOT_FOUND", message);
    }
}