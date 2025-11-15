package com.example.liskovbackend.dto.risk.analyze.response;

import com.example.liskovbackend.dto.gpt.request.DetailDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
@Builder
public class AnalyzeGenerateResponse {
    Integer totalRisk;
    String summary;
    List<DetailDto> details;
}
