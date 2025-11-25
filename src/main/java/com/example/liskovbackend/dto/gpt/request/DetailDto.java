package com.example.liskovbackend.dto.gpt.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
public class DetailDto {
    private String title;
    private String content;
}
