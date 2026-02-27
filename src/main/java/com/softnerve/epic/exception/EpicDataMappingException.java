package com.softnerve.epic.exception;

public class EpicDataMappingException extends RuntimeException {
    public EpicDataMappingException(String message) {
        super(message);
    }
    public EpicDataMappingException(String message, Throwable cause) {
        super(message, cause);
    }
}

