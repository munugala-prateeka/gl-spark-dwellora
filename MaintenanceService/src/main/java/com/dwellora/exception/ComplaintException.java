package com.dwellora.exception;

/** Custom runtime exception thrown for business logic and validation failures in complaint processing. */
public class ComplaintException extends RuntimeException {

    public ComplaintException(String message) {
        super(message);
    }
}