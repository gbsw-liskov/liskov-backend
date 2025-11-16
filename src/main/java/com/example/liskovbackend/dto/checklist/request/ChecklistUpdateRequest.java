package com.example.liskovbackend.dto.checklist.request;

import com.example.liskovbackend.entity.Severity;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ChecklistUpdateRequest {
    private Long itemId;
    private String memo;
    private Severity severity;
}
