package com.example.liskovbackend.service;

import com.example.liskovbackend.dto.user.request.PasswordUpdateRequest;
import com.example.liskovbackend.dto.user.request.UserUpdateRequest;
import com.example.liskovbackend.dto.user.response.UserResponse;
import com.example.liskovbackend.entity.User;
import com.example.liskovbackend.global.exception.ResourceNotFoundException;
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
        var user = getAuthenticatedUser();
        return UserResponse.from(user);
    }

    @Transactional
    public UserResponse updateUser(UserUpdateRequest request) {
        var user = getAuthenticatedUser();

        user.updateUserInfo(
                request.firstName(),
                request.lastName()
        );

        var savedUser = userRepository.save(user);
        return UserResponse.from(savedUser);
    }

    @Transactional
    public void updatePassword(PasswordUpdateRequest request) {
        var user = getAuthenticatedUser();

        if (!passwordEncoder.matches(request.currentPassword(), user.getPassword())) {
            throw new BadCredentialsException("현재 비밀번호가 일치하지 않습니다.");
        }

        user.updatePassword(passwordEncoder.encode(request.newPassword()));

        userRepository.save(user);
    }

    @Transactional
    public void deleteUser() {
        var user = getAuthenticatedUser();
        userRepository.delete(user);
    }

    private User getAuthenticatedUser() {
        var userId = Long.valueOf(SecurityContextHolder.getContext().getAuthentication().getName());

        return userRepository.findById(userId)
            .orElseThrow(() -> new ResourceNotFoundException("사용자를 찾을 수 없습니다."));
    }
}
