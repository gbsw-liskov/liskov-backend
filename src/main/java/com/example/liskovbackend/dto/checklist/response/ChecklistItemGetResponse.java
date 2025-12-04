package com.example.liskovbackend.dto.checklist.response;

import com.example.liskovbackend.entity.Severity;
import lombok.Builder;

@Builder
public record ChecklistItemGetResponse(
    Long itemId,
    String content,
    Severity severity,
    String memo
) {}
