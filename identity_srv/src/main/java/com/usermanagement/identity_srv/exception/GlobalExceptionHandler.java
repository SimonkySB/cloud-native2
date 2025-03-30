package com.usermanagement.identity_srv.exception;

import java.time.LocalDateTime;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;

@RestControllerAdvice
public class GlobalExceptionHandler {

  @ExceptionHandler(ResponseStatusException.class)
  public ResponseEntity<?> handleResponseStatusException(ResponseStatusException ex) {
    return ResponseEntity
        .status(ex.getStatusCode())
        .body(Map.of(
            "timestamp", LocalDateTime.now().toString(),
            "status", ex.getStatusCode().value(),
            "error", HttpStatus.valueOf(ex.getStatusCode().value()).getReasonPhrase(),
            "message", ex.getReason(),
            "path", "" // se puede agregar vía HttpServletRequest si quieres
        ));
  }
}
