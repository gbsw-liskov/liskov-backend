package com.example.liskovbackend.dto.checklist.response;

import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record AllChecklistGetResponse(
    Long checklistId,
    Long propertyId,
    LocalDateTime createdAt,
    Integer itemCount
) {}
