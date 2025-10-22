package com.example.liskovbackend.dto.checklist.response;

import com.example.liskovbackend.enums.Severity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@AllArgsConstructor
public class ChecklistUpdateResponse {
    private Long checklistId;
    private Long propertyId;
    private Integer updatedItemCount;
    private LocalDateTime updatedAt;
}
