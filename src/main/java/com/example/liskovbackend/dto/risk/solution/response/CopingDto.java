package com.example.liskovbackend.dto.risk.solution.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
public class CopingDto {
    private String title;
    private String list;
}