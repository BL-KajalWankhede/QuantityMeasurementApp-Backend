package com.quantitymeasurement.exception;

import com.quantitymeasurement.auth.AuthFlowException;
import jakarta.servlet.http.HttpServletRequest;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.web.servlet.resource.NoResourceFoundException;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.HttpRequestMethodNotSupportedException;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {
    private static final Logger log = LogManager.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleMethodArgumentNotValidException(
            MethodArgumentNotValidException exception, HttpServletRequest request) {
        log.trace("Handling validation exception");
        String message = exception.getBindingResult().getAllErrors().stream()
                .findFirst()
                .map(error -> error instanceof FieldError fieldError
                        ? fieldError.getDefaultMessage()
                        : error.getDefaultMessage())
                .orElse("Validation failed");
        log.warn("Validation failed: {}", message);
        return buildResponse(HttpStatus.BAD_REQUEST, "Quantity Measurement Error", message, request.getRequestURI());
    }

    @ExceptionHandler(QuantityMeasurementException.class)
    public ResponseEntity<Map<String, Object>> handleQuantityException(
            QuantityMeasurementException exception, HttpServletRequest request) {
        log.trace("Handling math exception");
        log.warn("Math error: {}", exception.getMessage());
        return buildResponse(HttpStatus.BAD_REQUEST, "Quantity Measurement Error",
                exception.getMessage(), request.getRequestURI());
    }

    @ExceptionHandler(AuthFlowException.class)
    public ResponseEntity<Map<String, Object>> handleAuthException(
            AuthFlowException exception, HttpServletRequest request) {
        log.trace("Handling auth exception");
        log.warn("Auth error: {}", exception.getMessage());
        return buildResponse(HttpStatus.BAD_REQUEST, "Authentication Error",
                exception.getMessage(), request.getRequestURI());
    }

    @ExceptionHandler(NoHandlerFoundException.class)
    public ResponseEntity<Map<String, Object>> handleNoHandlerFoundException(
            NoHandlerFoundException exception, HttpServletRequest request) {
        log.warn("404 Not Found: {}", exception.getMessage());
        return buildResponse(HttpStatus.NOT_FOUND, "Not Found",
                "The requested resource was not found on this server", request.getRequestURI());
    }

    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<Map<String, Object>> handleNoResourceFoundException(
            NoResourceFoundException exception, HttpServletRequest request) {
        log.warn("404 Resource Not Found: {}", exception.getMessage());
        return buildResponse(HttpStatus.NOT_FOUND, "Not Found",
                "The requested API endpoint was not found on this server", request.getRequestURI());
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<Map<String, Object>> handleHttpMessageNotReadableException(
            HttpMessageNotReadableException exception, HttpServletRequest request) {
        log.warn("Malformed JSON request: {}", exception.getMessage());
        return buildResponse(HttpStatus.BAD_REQUEST, "Bad Request",
                "Malformed JSON request or invalid data format", request.getRequestURI());
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<Map<String, Object>> handleHttpRequestMethodNotSupportedException(
            HttpRequestMethodNotSupportedException exception, HttpServletRequest request) {
        log.warn("Method not supported: {}", exception.getMessage());
        return buildResponse(HttpStatus.METHOD_NOT_ALLOWED, "Method Not Allowed",
                exception.getMessage(), request.getRequestURI());
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleResourceNotFoundException(
            ResourceNotFoundException exception, HttpServletRequest request) {
        log.warn("Resource not found: {}", exception.getMessage());
        return buildResponse(HttpStatus.NOT_FOUND, "Not Found",
                exception.getMessage(), request.getRequestURI());
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, Object>> handleIllegalArgumentException(
            IllegalArgumentException exception, HttpServletRequest request) {
        log.warn("Illegal argument: {}", exception.getMessage());
        return buildResponse(HttpStatus.BAD_REQUEST, "Bad Request",
                exception.getMessage(), request.getRequestURI());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleGlobalException(
            Exception exception, HttpServletRequest request) {
        log.trace("Handling unexpected global exception");
        log.fatal("Unexpected global error: {}", exception.getMessage());
        log.error("Error details", exception);
        return buildResponse(HttpStatus.INTERNAL_SERVER_ERROR, "Internal Server Error",
                exception.getMessage(), request.getRequestURI());
    }

    private ResponseEntity<Map<String, Object>> buildResponse(
            HttpStatus status, String error, String message, String path) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("timestamp", LocalDateTime.now());
        body.put("status", status.value());
        body.put("error", error);
        body.put("message", message);
        body.put("path", path);
        return ResponseEntity.status(status).body(body);
    }
}