package com.example.liskovbackend.dto.checklist.request;

import jakarta.validation.constraints.NotNull;
import java.util.List;

public record ChecklistSaveRequest(
    @NotNull Long propertyId,
    @NotNull List<String> contents
) {}
