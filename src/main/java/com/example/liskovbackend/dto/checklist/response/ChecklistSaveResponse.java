package com.example.liskovbackend.dto.checklist.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@AllArgsConstructor
public class ChecklistSaveResponse {
    private Long checklistId;
    private Long propertyId;
    private Integer itemCount;
    private LocalDateTime createdAt;
}
