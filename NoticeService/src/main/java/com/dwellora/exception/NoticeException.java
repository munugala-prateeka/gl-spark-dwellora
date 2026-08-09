package com.dwellora.exception;

/** Custom runtime exception thrown for business logic and validation failures in notices. */
public class NoticeException extends RuntimeException {

    public NoticeException(String message) {
        super(message);
    }
}