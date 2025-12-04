package com.example.liskovbackend.dto.user.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;

@Builder
public record UserUpdateRequest(

    @NotBlank(message = "이름은 필수 입력 항목입니다.")
    String firstName,

    @NotBlank(message = "성은 필수 입력 항목입니다.")
    String lastName
) {}
