package com.shakti.microservices.common_libs.Exceptions;

import com.shakti.microservices.common_libs.Dtos.ErrorResponse;

public class ErrorResponseBuilder {

    // Build error response from CustomException
    public static ErrorResponse build(CustomException ex, String path) {
        return ErrorResponse.of(
                ex.getStatus(),
                ex.getError(),
                ex.getMessage(),
                path
        );
    }

    // Build error response for general exceptions
    public static ErrorResponse buildGeneral(Exception ex, String path) {
        return ErrorResponse.of(
                500,
                "INTERNAL_SERVER_ERROR",
                ex.getMessage(),
                path
        );
    }
}