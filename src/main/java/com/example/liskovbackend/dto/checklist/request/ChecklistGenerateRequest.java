package com.example.liskovbackend.dto.checklist.request;

import com.example.liskovbackend.dto.model.Message;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class ChecklistGenerateRequest {
    private Long propertyId;
}
