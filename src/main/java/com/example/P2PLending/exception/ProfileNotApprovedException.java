package com.example.P2PLending.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.FORBIDDEN) // 403 Forbidden
public class ProfileNotApprovedException extends RuntimeException {
    public ProfileNotApprovedException(String message) {
        super(message);
    }
}