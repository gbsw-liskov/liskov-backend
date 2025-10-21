package com.example.liskovbackend.dto;

import com.example.liskovbackend.enums.Severity;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ChecklistUpdateRequest {
    private Long itemId;
    private String memo;
    private Severity severity;
}
