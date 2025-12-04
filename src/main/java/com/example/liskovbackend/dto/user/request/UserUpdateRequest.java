package com.example.liskovbackend.dto.user.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserUpdateRequest {
    
    @NotBlank(message = "이름은 필수 입력 항목입니다.")
    private String firstName;
    
    @NotBlank(message = "성은 필수 입력 항목입니다.")
    private String lastName;
}
