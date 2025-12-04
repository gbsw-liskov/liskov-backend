package com.example.liskovbackend.dto.checklist.response;

import java.util.List;

public record ChecklistGenerateResponse(
    List<String> content
) {}
