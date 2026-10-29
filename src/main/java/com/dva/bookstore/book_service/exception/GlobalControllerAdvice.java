package com.dva.bookstore.book_service.exception;

import java.time.Instant;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import lombok.extern.slf4j.Slf4j;
import java.util.LinkedHashMap;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.ErrorResponse;

@RestControllerAdvice
@Slf4j
public class GlobalControllerAdvice {

    @ExceptionHandler(ResourceNotFoundException.class)
    public ProblemDetail handleResourceNotFound(ResourceNotFoundException ex) {
        log.warn("Resource not found: {}", ex.getMessage());
        ProblemDetail problem = build(HttpStatus.NOT_FOUND, "Resource Not Found", ex.getMessage());
        problem.setProperty("resource", ex.getResourceName());
        problem.setProperty("field", ex.getFieldName());
        problem.setProperty("value", ex.getFieldValue());
        return problem;
    }

    @ExceptionHandler(DuplicateResourceException.class)
    public ProblemDetail handleDuplicate(DuplicateResourceException ex) {
        log.warn("Duplicate resource: {}", ex.getMessage());
        return build(HttpStatus.CONFLICT, "Duplicate Resource", ex.getMessage());
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ProblemDetail handleIntegrity(DataIntegrityViolationException ex) {
        log.warn("Data integrity violation: {}", ex.getMostSpecificCause().getMessage());
        return build(HttpStatus.CONFLICT, "Data Integrity Violation",
                "The operation violates a data constraint (e.g. the record is in use)");
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ProblemDetail handleValidation(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new LinkedHashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(e -> {
            log.warn("Validation failed {} : {}", e.getField(), e.getDefaultMessage());
            errors.putIfAbsent(e.getField(), e.getDefaultMessage());
        });
        ProblemDetail problem = build(HttpStatus.BAD_REQUEST, "Validation Error",
                "Validation failed in one or more fields");
        problem.setProperty("errors", errors);
        return problem;
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ProblemDetail handleUnreadable(HttpMessageNotReadableException ex) {
        return build(HttpStatus.BAD_REQUEST, "Malformed Request", "Request body is missing or malformed");
    }

    @ExceptionHandler(Exception.class)
    public ProblemDetail handleException(Exception ex) {
        if (ex instanceof ErrorResponse errorResponse) {
            ProblemDetail body = errorResponse.getBody();
            body.setProperty("timestamp", Instant.now());
            return body;
        }
        log.error("Unexpected error", ex);
        return build(HttpStatus.INTERNAL_SERVER_ERROR, "Internal Server Error",
                "Unexpected error, please contact the administrator");
    }

    private ProblemDetail build(HttpStatus status, String title, String detail) {
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(status, detail);
        problem.setTitle(title);
        problem.setProperty("timestamp", Instant.now());
        return problem;
    }

    @ExceptionHandler(InvalidFileException.class)
    public ProblemDetail handleInvalidFile(InvalidFileException ex) {
        log.warn("Invalid file: {}", ex.getMessage());
        return build(HttpStatus.BAD_REQUEST, "Invalid File", ex.getMessage());
    }
}
