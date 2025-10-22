package com.example.liskovbackend.dto.checklist.request;

import lombok.Builder;
import lombok.Getter;
import jakarta.validation.constraints.NotNull;

import java.util.List;

@Getter
@Builder
public class ChecklistSaveRequest {
    @NotNull
    private Long propertyId;

    @NotNull
    private List<ChecklistItemsSaveRequest> items;
}
