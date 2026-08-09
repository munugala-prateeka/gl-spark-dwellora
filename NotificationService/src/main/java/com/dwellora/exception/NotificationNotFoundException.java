package com.dwellora.exception;

/**
 * Custom runtime exception thrown when a requested notification cannot be found.
 */
public class NotificationNotFoundException extends RuntimeException {

    public NotificationNotFoundException(String message) {
        super(message);
    }
}