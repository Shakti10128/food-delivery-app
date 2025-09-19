package com.shakti.microservices.common_libs.Exceptions;

public class InternalServerErrorException extends CustomException {
    public InternalServerErrorException(String message) {
        super(500, "INTERNAL_SERVER_ERROR", message);
    }
}