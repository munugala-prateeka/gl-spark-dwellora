package com.dwellora.exception;

/**
 * Custom runtime exception thrown for onboarding-related errors.
 */
public class OnboardingException extends RuntimeException {

    public OnboardingException(String message) {
        super(message);
    }
}