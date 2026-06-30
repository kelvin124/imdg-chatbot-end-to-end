package com.example.demo.controller.advice;

import com.example.demo.service.exception.RecordNotFoundException;
import com.example.demo.service.exception.StowagePlanException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestControllerAdvice
public class ApiExceptionHandler {

    private ApiErrorResponse buildApiErrorResponse(HttpStatus status, HttpServletRequest request,
                                                   String error, Map<String, Object> additionalProperties) {
        return new ApiErrorResponse(
                error, request.getRequestURI(),
                additionalProperties, status, Instant.now()
        );
    }

    @ExceptionHandler(StowagePlanException.class)
    public ResponseEntity<ApiErrorResponse> handleStowagePlanException(StowagePlanException ex, HttpServletRequest request) {
        return ResponseEntity.badRequest().body(
                this.buildApiErrorResponse(
                        HttpStatus.BAD_REQUEST, request,
                        ex.getMessage(), ex.getProperties()
                )
        );
    }

    @ExceptionHandler(RecordNotFoundException.class)
    public ResponseEntity<ApiErrorResponse> RecordNotFoundException(RecordNotFoundException ex, HttpServletRequest request) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                this.buildApiErrorResponse(
                        HttpStatus.NOT_FOUND, request,
                        ex.getMessage(), ex.getProperties()
                )
        );
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiErrorResponse> handleValidationExceptions(MethodArgumentNotValidException ex, HttpServletRequest request) {
        List<FieldError> errors = ex.getBindingResult().getFieldErrors();
        Map<String, Object> fieldErrorsMap = errors.stream().collect(Collectors.toMap(
                FieldError::getField,
                error -> "rejected value: " + error.getRejectedValue()
        ));
        return ResponseEntity.badRequest().body(
                this.buildApiErrorResponse(
                        HttpStatus.BAD_REQUEST, request,
                        "Validation failed for one or more fields", fieldErrorsMap
                )
        );
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiErrorResponse> handleGenericException(Exception ex, HttpServletRequest request) {
        return ResponseEntity.internalServerError().body(
                this.buildApiErrorResponse(
                        HttpStatus.INTERNAL_SERVER_ERROR, request,
                        ex.getMessage(), null)
        );
    }

}
