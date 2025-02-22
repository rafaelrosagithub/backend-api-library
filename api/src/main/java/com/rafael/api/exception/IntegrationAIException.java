package com.rafael.api.exception;

import java.io.Serial;

public class IntegrationAIException extends RuntimeException {

    @Serial
    private static final long serialVersionUID = 1L;

    private int statusCode;

    public IntegrationAIException(String message, int statusCode) {
        super(message);
        this.statusCode= statusCode;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(int status) {
        this.statusCode = statusCode;
    }

}

