package com.yourbank.exceptions;

public class FineNotFoundException extends RuntimeException {
    public FineNotFoundException(String message) {
        super(message);
    }
}


