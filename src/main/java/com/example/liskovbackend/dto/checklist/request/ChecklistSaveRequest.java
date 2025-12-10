package com.example.liskovbackend.dto.checklist.request;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;

import java.util.List;

@Builder
public record ChecklistSaveRequest(
    @NotNull Long propertyId,
    @NotNull List<ChecklistItemsSaveRequest> items
) {}
