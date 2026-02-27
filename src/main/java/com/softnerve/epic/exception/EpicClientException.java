package com.softnerve.epic.exception;

public class EpicClientException extends RuntimeException {
    private final int statusCode;
    public EpicClientException(int statusCode, String message) {
        super(message);
        this.statusCode = statusCode;
    }
    public EpicClientException(int statusCode, String message, Throwable cause) {
        super(message, cause);
        this.statusCode = statusCode;
    }
    public int getStatusCode() {
        return statusCode;
    }
}

