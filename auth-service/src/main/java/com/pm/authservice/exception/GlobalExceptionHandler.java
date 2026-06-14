package com.pm.authservice.exception;

import io.jsonwebtoken.JwtException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(
            MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, Object> validation(
            MethodArgumentNotValidException ex) {

        return Map.of(
                "timestamp", LocalDateTime.now(),
                "status", 400,
                "message",
                ex.getBindingResult()
                        .getFieldError()
                        .getDefaultMessage()
        );
    }

    @ExceptionHandler(JwtException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public Map<String, Object> jwt(
            JwtException ex) {

        return Map.of(
                "timestamp", LocalDateTime.now(),
                "status", 401,
                "message", ex.getMessage()
        );
    }
}