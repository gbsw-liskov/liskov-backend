package com.example.liskovbackend.dto.auth.response;

import lombok.Builder;

@Builder
public record AuthResponse(
    String accessToken,
    String refreshToken
) {}
