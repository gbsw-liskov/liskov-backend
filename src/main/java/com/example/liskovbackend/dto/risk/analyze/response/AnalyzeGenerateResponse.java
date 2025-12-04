package com.example.liskovbackend.dto.risk.analyze.response;

import com.example.liskovbackend.dto.gpt.request.DetailDto;
import java.util.List;

public record AnalyzeGenerateResponse(
    Integer totalRisk,
    String summary,
    List<DetailDto> details
) {}
