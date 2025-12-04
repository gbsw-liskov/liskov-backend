package com.example.liskovbackend.dto.user.response;

import com.example.liskovbackend.entity.User;
import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record UserResponse(
    Long id,
    String email,
    String firstName,
    String lastName,
    LocalDateTime createdAt,
    LocalDateTime updatedAt
) {
    public static UserResponse from(User user) {
        return UserResponse.builder()
            .id(user.getId())
            .email(user.getEmail())
            .firstName(user.getFirstName())
            .lastName(user.getLastName())
            .createdAt(user.getCreatedAt())
            .updatedAt(user.getUpdatedAt())
            .build();
    }
}
