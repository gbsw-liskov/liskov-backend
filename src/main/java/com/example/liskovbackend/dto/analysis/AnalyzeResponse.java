package com.example.liskovbackend.dto.analysis;

import java.util.List;

public record AnalyzeResponse(
    Integer totalRisk,
    List<Detail> details,
    String comment
) {
    public record Detail(
        String original,
        String analysisText
    ) {}
}
