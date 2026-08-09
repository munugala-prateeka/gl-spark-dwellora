package com.dwellora.exception;

/**
 * Exception thrown when a requested booking record cannot be found in the database.
 */
public class BookingNotFoundException extends RuntimeException {

    public BookingNotFoundException(String message) {
        super(message);
    }
}