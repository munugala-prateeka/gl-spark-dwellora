package com.dwellora.exception;

/** Custom exception thrown during amenity domain errors. */
public class AmenityException extends RuntimeException {

    public AmenityException(String message) {
        super(message);
    }
}