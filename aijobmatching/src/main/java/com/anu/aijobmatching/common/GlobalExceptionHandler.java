package com.anu.aijobmatching.common;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import com.anu.aijobmatching.user.exception.InvalidCredentialsException;
import com.anu.aijobmatching.user.exception.UserAlreadyExistsException;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(UserAlreadyExistsException.class)
    public ResponseEntity<?> handleUserExists(UserAlreadyExistsException ex) {
        Map<String, String> body = new HashMap<>();
        body.put("error", ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidation(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(err -> errors.put(err.getField(), err.getDefaultMessage()));
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errors);
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
