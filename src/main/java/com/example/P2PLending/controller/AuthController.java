package com.example.P2PLending.controller;

import com.example.P2PLending.dto.auth.JwtAuthResponse;
import com.example.P2PLending.dto.auth.LoginRequest;
import com.example.P2PLending.dto.auth.RegisterRequest;
import com.example.P2PLending.service.AuthService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<JwtAuthResponse> register(@Valid @RequestBody RegisterRequest request) {
        // Tạm thời, ta cho phép đăng ký cả 3 loại role
        // Trong thực tế, có thể chỉ cho đăng ký BORROWER/LENDER
        return ResponseEntity.ok(authService.register(request));
    }

    @PostMapping("/login")
    public ResponseEntity<JwtAuthResponse> login( @Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }
}