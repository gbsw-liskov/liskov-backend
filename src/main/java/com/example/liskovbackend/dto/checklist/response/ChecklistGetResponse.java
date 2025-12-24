package com.example.liskovbackend.dto.checklist.response;

import lombok.Builder;

import java.util.List;

@Builder
public record ChecklistGetResponse(
    Long checklistId,
    Long propertyId,
    String name,
    List<ChecklistItemGetResponse> items
) {}
