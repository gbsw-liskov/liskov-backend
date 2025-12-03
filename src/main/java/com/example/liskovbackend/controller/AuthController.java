package com.example.liskovbackend.controller;

import com.example.liskovbackend.dto.auth.request.LoginRequest;
import com.example.liskovbackend.dto.auth.request.SignupRequest;
import com.example.liskovbackend.dto.auth.response.AuthResponse;
import com.example.liskovbackend.dto.auth.response.MessageResponse;
import com.example.liskovbackend.service.AuthService;
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
    
    @PostMapping("/signup")
    public ResponseEntity<MessageResponse> signup(@Valid @RequestBody SignupRequest signupRequest) {
        authService.signup(signupRequest);
        return ResponseEntity.ok(new MessageResponse("회원가입이 완료되었습니다."));
    }
    
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest loginRequest) {
        var authResponse = authService.login(loginRequest);
        return ResponseEntity.ok(authResponse);
    }
}
