package com.anu.aijobmatching.common.exception;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;
import java.time.Instant;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import com.anu.aijobmatching.user.exception.InvalidCredentialsException;
import com.anu.aijobmatching.user.exception.UserAlreadyExistsException;

import jakarta.servlet.http.HttpServletRequest;

import com.anu.aijobmatching.user.exception.AuthenticationException;
import com.anu.aijobmatching.common.api.ApiError;
import com.anu.aijobmatching.user.exception.AccessDeniedException;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(UserAlreadyExistsException.class)
    public ResponseEntity<?> handleUserExists(UserAlreadyExistsException ex) {
        Map<String, String> body = new HashMap<>();
        body.put("error", ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiError> handleValidation(MethodArgumentNotValidException ex, HttpServletRequest req) {
            Map<String, String> fieldErrors = new LinkedHashMap<>();
            for (FieldError fe : ex.getBindingResult().getFieldErrors()) {
                fieldErrors.put(fe.getField(), fe.getDefaultMessage());
            }
            String traceId = UUID.randomUUID().toString();

            ApiError body = ApiError.of(
                  "VALIDATION_ERROR",
                  req.getRequestURI(),
                  traceId,
                    fieldErrors,
                    "Request validation failed",
                    HttpStatus.BAD_REQUEST.value()
                    
            );
            return ResponseEntity.badRequest().body(body);
        }

    @ExceptionHandler(UsernameNotFoundException.class)
    public ResponseEntity<Map<String, String>> handleNotFound(UsernameNotFoundException ex) {
        Map<String, String> body = new HashMap<>();
        body.put("error", ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(body);
    }

    @ExceptionHandler(InvalidCredentialsException.class)
    public ResponseEntity<Map<String, String>> handleBadCreds(InvalidCredentialsException ex) {
        Map<String, String> body = new HashMap<>();
        body.put("error", ex.getMessage());
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(body);
    }

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<?> handleAuth(AuthenticationException ex) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(
            Map.of(
                "timestamp", Instant.now(),
                "status", 401,
                "error", "UNAUTHORIZED",
                "message", ex.getMessage()
            )
        );
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<?> handleAccessDenied(AccessDeniedException ex) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(
            Map.of(
                "timestamp", Instant.now(),
                "status", 403,
                "error", "FORBIDDEN",
                "message", "You are not allowed to access this resource"
            )
        );
    }

}
