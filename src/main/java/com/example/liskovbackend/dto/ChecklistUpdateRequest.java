package com.example.liskovbackend.dto;

import com.example.liskovbackend.enums.Severity;
import lombok.Getter;

@Getter
public class ChecklistUpdateRequest {
    private Long itemId;
    private String memo;
    private Severity severity;
}
