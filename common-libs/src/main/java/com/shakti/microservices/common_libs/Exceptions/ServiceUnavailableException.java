package com.shakti.microservices.common_libs.Exceptions;

public class ServiceUnavailableException extends CustomException {
    public ServiceUnavailableException(String message) {
        super(503, "SERVICE_UNAVAILABLE", message);
    }
}