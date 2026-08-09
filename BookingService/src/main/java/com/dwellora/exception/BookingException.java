package com.dwellora.exception;

/**
 * Exception thrown when business rules or constraint checks fail during booking operations.
 */
public class BookingException extends RuntimeException {

    public BookingException(String message) {
        super(message);
    }
}