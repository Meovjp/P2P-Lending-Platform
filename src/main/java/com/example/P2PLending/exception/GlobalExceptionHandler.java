package com.example.P2PLending.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;

@RestControllerAdvice // Bắt tất cả lỗi từ @RestController
public class GlobalExceptionHandler {

    // Bắt lỗi 403 của chúng ta
    @ExceptionHandler(ProfileNotApprovedException.class)
    public ResponseEntity<Map<String, String>> handleProfileNotApproved(ProfileNotApprovedException ex) {
        Map<String, String> error = Map.of("error", ex.getMessage());
        return new ResponseEntity<>(error, HttpStatus.FORBIDDEN);
    }

    // Bắt các lỗi NullPointerException (như lỗi 'role' null)
    @ExceptionHandler(NullPointerException.class)
    public ResponseEntity<Map<String, String>> handleNullPointer(NullPointerException ex) {
        Map<String, String> error = Map.of("error", "Lỗi máy chủ: " + ex.getMessage());
        return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
    }
    
    // Bắt các lỗi RuntimeException chung (để không bị rò rỉ)
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<Map<String, String>> handleGenericRuntime(RuntimeException ex) {
        Map<String, String> error = Map.of("error", "Lỗi không xác định: " + ex.getMessage());
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }
}