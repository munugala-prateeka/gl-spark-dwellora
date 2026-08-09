package com.dwellora.utility;

import com.dwellora.exception.BookingException;
import com.dwellora.exception.BookingNotFoundException;
import java.util.LinkedHashMap;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * Controller advice providing global exception handling and custom error response formatting across controller endpoints.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Handles validation errors triggered by Spring request body validation.
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorInfo> handleValidationException(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new LinkedHashMap<>();
        ex.getBindingResult()
                .getFieldErrors()
                .forEach(error -> errors.put(error.getField(), error.getDefaultMessage()));

        ErrorInfo errorInfo = new ErrorInfo("Validation Failed", errors.toString());
        return new ResponseEntity<>(errorInfo, HttpStatus.BAD_REQUEST);
    }

    /**
     * Handles booking resource missing exceptions.
     */
    @ExceptionHandler(BookingNotFoundException.class)
    public ResponseEntity<ErrorInfo> handleBookingNotFoundException(
            BookingNotFoundException ex) {
        ErrorInfo errorInfo = new ErrorInfo("Booking Not Found", ex.getMessage());
        return new ResponseEntity<>(errorInfo, HttpStatus.NOT_FOUND);
    }

    /**
     * Handles domain and business rule validation exceptions.
     */
    @ExceptionHandler(BookingException.class)
    public ResponseEntity<ErrorInfo> handleBookingException(BookingException ex) {
        ErrorInfo errorInfo = new ErrorInfo("Booking Business Error", ex.getMessage());
        return new ResponseEntity<>(errorInfo, HttpStatus.BAD_REQUEST);
    }

    /**
     * Handles unexpected system exceptions.
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorInfo> handleGeneralException(Exception ex) {
        ErrorInfo errorInfo = new ErrorInfo("Internal Server Error", ex.getMessage());
        return new ResponseEntity<>(errorInfo, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}