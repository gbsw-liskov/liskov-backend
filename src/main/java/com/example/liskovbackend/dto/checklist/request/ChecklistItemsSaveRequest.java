package com.example.liskovbackend.dto.checklist.request;

import jakarta.validation.constraints.NotNull;

public record ChecklistItemsSaveRequest(
    @NotNull String content,
    String severity,
    String memo
) {}
