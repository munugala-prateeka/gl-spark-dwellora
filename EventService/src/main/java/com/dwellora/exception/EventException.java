package com.dwellora.exception;

/** Custom runtime exception thrown for event processing and domain constraint violations. */
public class EventException extends RuntimeException {

    public EventException(String message) {
        super(message);
    }
}