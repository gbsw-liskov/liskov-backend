package com.example.liskovbackend.dto.checklist.request;

import com.example.liskovbackend.entity.Severity;
import lombok.Builder;
import lombok.Getter;
import jakarta.validation.constraints.NotNull;

@Getter
@Builder
public class ChecklistItemsSaveRequest {
    @NotNull
    private String content;

    @NotNull
    private Severity severity;
}
