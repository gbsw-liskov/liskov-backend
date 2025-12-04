package com.example.liskovbackend.service;

import com.example.liskovbackend.dto.user.request.PasswordUpdateRequest;
import com.example.liskovbackend.dto.user.request.UserUpdateRequest;
import com.example.liskovbackend.dto.user.response.UserResponse;
import com.example.liskovbackend.entity.User;
import com.example.liskovbackend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {
    
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    
    @Transactional(readOnly = true)
    public UserResponse getCurrentUser() {
        User user = getAuthenticatedUser();
        return UserResponse.fromEntity(user);
    }
    
    @Transactional
    public UserResponse updateUser(UserUpdateRequest request) {
        User user = getAuthenticatedUser();
        
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setUpdatedAt(LocalDateTime.now());
        
        User savedUser = userRepository.save(user);
        log.info("User updated successfully: {}", savedUser.getEmail());
        
        return UserResponse.fromEntity(savedUser);
    }
    
    @Transactional
    public void updatePassword(PasswordUpdateRequest request) {
        User user = getAuthenticatedUser();
        
        // 현재 비밀번호 확인
        if (!passwordEncoder.matches(request.getCurrentPassword(), user.getPassword())) {
            log.warn("Password update failed: Current password doesn't match for user {}", user.getEmail());
            throw new BadCredentialsException("현재 비밀번호가 일치하지 않습니다.");
        }
        
        // 새 비밀번호 설정
        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        user.setUpdatedAt(LocalDateTime.now());
        
        userRepository.save(user);
        log.info("Password updated successfully for user: {}", user.getEmail());
    }
    
    @Transactional
    public void deleteUser() {
        User user = getAuthenticatedUser();
        
        // 소프트 삭제 처리
        user.setIsDeleted(true);
        user.setUpdatedAt(LocalDateTime.now());
        
        userRepository.save(user);
        log.info("User deleted (soft delete) successfully: {}", user.getEmail());
    }
    
    private User getAuthenticatedUser() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> {
                    log.error("User not found with email: {}", email);
                    return new IllegalArgumentException("사용자를 찾을 수 없습니다.");
                });
    }
}
