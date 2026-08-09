package com.dwellora.utility;

import com.dwellora.exception.ComplaintException;
import java.util.LinkedHashMap;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/** Global exception handler advice for mapping service exceptions to structured HTTP responses. */
@RestControllerAdvice
public class GlobalExceptionHandler {

    /** Handles validation exceptions caused by invalid request body constraints. */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorInfo> handleValidationException(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new LinkedHashMap<>();
        ex.getBindingResult()
                .getFieldErrors()
                .forEach(error -> errors.put(error.getField(), error.getDefaultMessage()));
        return new ResponseEntity<>(
                new ErrorInfo("Validation Failed", errors.toString()), HttpStatus.BAD_REQUEST);
    }

    /** Handles custom ComplaintException instances thrown during business processing. */
    @ExceptionHandler(ComplaintException.class)
    public ResponseEntity<ErrorInfo> handleComplaintException(ComplaintException ex) {
        return new ResponseEntity<>(
                new ErrorInfo("Complaint Error", ex.getMessage()), HttpStatus.BAD_REQUEST);
    }

    /** Handles all unhandled general exceptions and returns a generic server error response. */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorInfo> handleGeneralException(Exception ex) {
        return new ResponseEntity<>(
                new ErrorInfo("Internal Server Error", ex.getMessage()),
                HttpStatus.INTERNAL_SERVER_ERROR);
    }
}