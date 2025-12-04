package com.example.liskovbackend.controller;

import com.example.liskovbackend.dto.user.request.PasswordUpdateRequest;
import com.example.liskovbackend.dto.user.request.UserUpdateRequest;
import com.example.liskovbackend.dto.user.response.UserResponse;
import com.example.liskovbackend.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {
    
    private final UserService userService;
    
    @GetMapping("/me")
    public ResponseEntity<ApiResponse<UserResponse>> getCurrentUser() {
        UserResponse userResponse = userService.getCurrentUser();
        return ApiResponse.ok(userResponse);
    }
    
    @PutMapping("/me")
    public ResponseEntity<ApiResponse<UserResponse>> updateUser(@Valid @RequestBody UserUpdateRequest request) {
        UserResponse updatedUser = userService.updateUser(request);
        return ApiResponse.ok(updatedUser);
    }
    
    @PutMapping("/me/password")
    public ResponseEntity<ApiResponse<Void>> updatePassword(@Valid @RequestBody PasswordUpdateRequest request) {
        userService.updatePassword(request);
        return ApiResponse.ok("비밀번호가 성공적으로 변경되었습니다.");
    }
    
    @DeleteMapping("/me")
    public ResponseEntity<ApiResponse<Void>> deleteUser() {
        userService.deleteUser();
        return ApiResponse.ok("계정이 성공적으로 삭제되었습니다.");
    }
}
