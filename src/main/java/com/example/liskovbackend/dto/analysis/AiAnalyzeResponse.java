package com.example.liskovbackend.dto.analysis;

import java.util.List;

public record AiAnalyzeResponse(
    Integer totalRisk,
    String summary,
    List<Detail> details
) {
    public record Detail(
        String title,
        String content,
        String severity
    ) {}
}
