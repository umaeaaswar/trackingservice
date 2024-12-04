package com.getrosoft.trackingservice.tracking_service.config;

import com.getrosoft.trackingservice.tracking_service.exceptions.DuplicateTrackingNumberException;
import com.getrosoft.trackingservice.tracking_service.exceptions.InvalidInputException;
import com.getrosoft.trackingservice.tracking_service.exceptions.TrackingIdNotFoundException;
import com.getrosoft.trackingservice.tracking_service.exceptions.TrackingNumberGenerationException;
import com.getrosoft.trackingservice.tracking_service.payload.ErrorResponse;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;


@ControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex,
                                                                  HttpHeaders headers,
                                                                  HttpStatusCode status,
                                                                  WebRequest request) {
        Map<String, String> errorMap = new LinkedHashMap<>();
        ex.getBindingResult().getAllErrors().forEach(error -> {
            String fieldName = ((FieldError) error).getField();
            String message = error.getDefaultMessage();
            errorMap.put(fieldName, message);
        });
        return new ResponseEntity<>(errorMap, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<Map<String, String>> handleConstraintViolation(ConstraintViolationException ex) {
        Map<String, String> errors = new HashMap<>();
        for (ConstraintViolation<?> violation : ex.getConstraintViolations()) {
            String fieldName = violation.getPropertyPath().toString();
            String errorMessage = violation.getMessage();
            errors.put(fieldName, errorMessage);
        }
        logger.warn("Validation failed: {}", errors);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errors);
    }

    @ExceptionHandler(InvalidInputException.class)
    public ResponseEntity<?> handleInvalidInputException(InvalidInputException ex) {
        logger.warn("Invalid input: {}", ex.getMessage());
        Throwable rootCause = this.findRootCause(ex);
        if (rootCause instanceof ConstraintViolationException) {
            return handleConstraintViolation((ConstraintViolationException) rootCause);
        }
        return createErrorResponse("Invalid Input Error", ex.getMessage(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(TrackingNumberGenerationException.class)
    public ResponseEntity<?> handleTrackingNumberGenerationException(TrackingNumberGenerationException ex) {
        logger.error("Tracking number generation failed: {}", ex.getMessage(), ex);
        Throwable rootCause = this.findRootCause(ex);
        if (rootCause instanceof ConstraintViolationException) {
            return handleConstraintViolation((ConstraintViolationException) rootCause);
        }
        return createErrorResponse("Tracking Number Generation Error", ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(DuplicateTrackingNumberException.class)
    public ResponseEntity<ErrorResponse> handleDuplicateTrackingNumberException(DuplicateTrackingNumberException ex) {
        logger.error("Duplicate tracking number detected: {}", ex.getMessage(), ex);
        return createErrorResponse("Duplicate Tracking Number Error", ex.getMessage(), HttpStatus.CONFLICT);
    }

    @ExceptionHandler(TrackingIdNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleTrackingIdNotFoundException(TrackingIdNotFoundException ex) {
        logger.error("Tracking ID not found: {}", ex.getMessage());
        return createErrorResponse("Tracking ID not found", ex.getMessage(), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<String> handleIllegalArgumentException(IllegalArgumentException ex) {
        return ResponseEntity.badRequest().body(ex.getMessage());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> handleGeneralException(Exception ex) {
        logger.error("Unhandled exception occurred: {}", ex.getMessage(), ex);

        // Check for ConstraintViolationException wrapped inside other exceptions
        Throwable rootCause = findRootCause(ex);
        if (rootCause instanceof ConstraintViolationException) {
            return handleConstraintViolation((ConstraintViolationException) rootCause);
        }

        return createErrorResponse("Internal Server Error", ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    private Throwable findRootCause(Throwable throwable) {
        Throwable rootCause = throwable;
        while (rootCause.getCause() != null && rootCause != rootCause.getCause()) {
            rootCause = rootCause.getCause();
        }
        return rootCause;
    }

    private ResponseEntity<ErrorResponse> createErrorResponse(final String error, final String message, final HttpStatus status) {
        final ErrorResponse errorResponse = new ErrorResponse(LocalDateTime.now(), message, error, status.value());
        return new ResponseEntity<>(errorResponse, status);
    }
}