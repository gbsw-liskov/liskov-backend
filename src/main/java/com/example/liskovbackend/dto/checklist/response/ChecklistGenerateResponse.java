package com.example.liskovbackend.dto.checklist.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@Builder
@NoArgsConstructor
public class ChecklistGenerateResponse {
    private List<String> content;
}
