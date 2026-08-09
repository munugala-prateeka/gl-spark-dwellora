package com.dwellora.utility;

import com.dwellora.exception.AmenityException;
import java.util.LinkedHashMap;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/** Global exception handler intercepting and formatting application-wide errors. */
@RestControllerAdvice
public class GlobalExceptionHandler {

    /** Handles bean validation errors and formats field error messages. */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorInfo> handleValidationException(
            MethodArgumentNotValidException ex) {
        Map<String, String> errors = new LinkedHashMap<>();
        ex.getBindingResult()
                .getFieldErrors()
                .forEach(error -> errors.put(error.getField(), error.getDefaultMessage()));

        ErrorInfo errorInfo = new ErrorInfo("Validation Failed", errors.toString());
        return new ResponseEntity<>(errorInfo, HttpStatus.BAD_REQUEST);
    }

    /** Handles custom business exceptions related to amenity operations. */
    @ExceptionHandler(AmenityException.class)
    public ResponseEntity<ErrorInfo> handleAmenityException(AmenityException ex) {
        ErrorInfo errorInfo = new ErrorInfo("Amenity Error", ex.getMessage());
        return new ResponseEntity<>(errorInfo, HttpStatus.BAD_REQUEST);
    }

    /** Handles uncaught runtime exceptions as internal server errors. */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorInfo> handleGeneralException(Exception ex) {
        ErrorInfo errorInfo = new ErrorInfo("Internal Server Error", ex.getMessage());
        return new ResponseEntity<>(errorInfo, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}