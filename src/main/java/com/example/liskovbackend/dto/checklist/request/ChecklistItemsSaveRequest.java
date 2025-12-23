package com.example.liskovbackend.dto.checklist.request;

import jakarta.validation.constraints.NotNull;

public record ChecklistItemsSaveRequest(
    String content,
    String severity,
    String memo
) {}
