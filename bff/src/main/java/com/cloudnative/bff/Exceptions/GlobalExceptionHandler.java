package com.cloudnative.bff.Exceptions;

import java.time.LocalDateTime;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.reactive.function.client.WebClientResponseException;

@RestControllerAdvice
public class GlobalExceptionHandler {

  @ExceptionHandler(WebClientResponseException.class)
  public ResponseEntity<?> handleResponseStatusException(WebClientResponseException ex) {
    return ResponseEntity
        .status(ex.getStatusCode())
        .body(Map.of(
            "timestamp", LocalDateTime.now().toString(),
            "status", ex.getStatusCode().value(),
            "error", HttpStatus.valueOf(ex.getStatusCode().value()).getReasonPhrase(),
            "message", ex.getMessage()
        ));
  }
}