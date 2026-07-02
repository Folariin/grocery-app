package com.mygroceries.backend.exception;
import com.mygroceries.backend.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(UserService.EmailAlreadyUsedException.class)
    public ResponseEntity<?> emailAlreadyUsed() {
        return ResponseEntity.status(409).body(Map.of("error", "EMAIL_ALREADY_USED"));
    }

    @ExceptionHandler(UserService.InvalidCredentialsException.class)
    public ResponseEntity<?> invalidCredentials() {
        return ResponseEntity.status(401).body(Map.of("error", "INVALID_CREDENTIALS"));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> generic(Exception ex) {
        return ResponseEntity.status(400).body(Map.of("error", "BAD_REQUEST"));
    }
}

