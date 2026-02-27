package com.softnerve.epic.exception.document;

public class FolderNotFoundException extends RuntimeException{
    public FolderNotFoundException(String message) {
        super(message);
    }
}
