package com.dwellora.exception;

/**
 * Custom runtime exception thrown for user-related errors.
 */
public class UserException extends RuntimeException {

    public UserException(String message) {
        super(message);
    }
}