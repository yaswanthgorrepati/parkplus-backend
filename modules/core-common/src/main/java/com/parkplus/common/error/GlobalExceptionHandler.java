package com.parkplus.common.error;

import jakarta.validation.ConstraintViolationException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiError> handleValidation(MethodArgumentNotValidException exception) {
        Map<String, String> fields = new HashMap<>();
        exception.getBindingResult().getFieldErrors().forEach(fe -> fields.put(fe.getField(), fe.getDefaultMessage()));

        return ResponseEntity.badRequest().body(
                ApiError.of(ErrorCodes.VALIDATION_ERROR, "Validation failed", 400, fields)
        );
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ApiError> handleConstraint(ConstraintViolationException exception) {
        Map<String, String> fields = new HashMap<>();
        exception.getConstraintViolations().forEach(cv -> fields.put(cv.getPropertyPath().toString(), cv.getMessage()));

        return ResponseEntity.badRequest().body(
                ApiError.of(ErrorCodes.VALIDATION_ERROR, "Validation failed", 400, fields)
        );
    }

    @ExceptionHandler(ApiException.class)
    public ResponseEntity<ApiError> handleApi(ApiException ex) {

        return ResponseEntity.status(ex.status()).body(
                ApiError.of(ex.code(), ex.getMessage(), ex.status(), null));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiError> handleOther(Exception exception) {
        return ResponseEntity.status(500).body(
                ApiError.of(ErrorCodes.INTERNAL_ERROR, "Unexpected error", 500, null)
        );
    }
}