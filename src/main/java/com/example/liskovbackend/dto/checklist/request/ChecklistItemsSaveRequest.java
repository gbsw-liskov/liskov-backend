package com.example.liskovbackend.dto.checklist.request;

import com.example.liskovbackend.entity.Severity;
import jakarta.validation.constraints.NotNull;

public record ChecklistItemsSaveRequest(
    @NotNull String content,
    @NotNull Severity severity
) {}
