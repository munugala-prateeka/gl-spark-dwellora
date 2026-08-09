package com.dwellora.exception;

/**
 * Custom runtime exception thrown for apartment-related errors.
 */
public class ApartmentException extends RuntimeException {

    public ApartmentException(String message) {
        super(message);
    }
}