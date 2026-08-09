package com.dwellora.exception;

/**
 * Custom runtime exception thrown for general notification errors.
 */
public class NotificationException extends RuntimeException {

    public NotificationException(String message) {
        super(message);
    }
}