package com.softnerve.epic.exception;

public class EpicAuthException extends RuntimeException {
    public EpicAuthException(String message) {
        super(message);
    }
    public EpicAuthException(String message, Throwable cause) {
        super(message, cause);
    }
}

