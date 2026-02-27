package com.softnerve.epic.exception;

public class PatientAlreadyPresentException extends RuntimeException {
    public PatientAlreadyPresentException(String message) {
        super(message);
    }
}
