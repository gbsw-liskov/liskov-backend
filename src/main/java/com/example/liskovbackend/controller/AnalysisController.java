package com.example.liskovbackend.controller;

import com.example.liskovbackend.common.ApiResponse;
import com.example.liskovbackend.dto.analysis.AnalyzeRequest;
import com.example.liskovbackend.dto.analysis.AnalyzeResponse;
import com.example.liskovbackend.service.AnalysisService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/analysis")
public class AnalysisController {

    private final AnalysisService analysisService;

    @PostMapping
    public ResponseEntity<ApiResponse<AnalyzeResponse>> analyze(
        @RequestPart("request") AnalyzeRequest request,
        @RequestPart(value = "files", required = false) List<MultipartFile> files
    ) {
        var response = analysisService.analyze(request, files);
        return ApiResponse.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<AnalyzeResponse>> getAnalysis(@PathVariable Long id) {
        var response = analysisService.getAnalysis(id);
        return ApiResponse.ok(response);
    }
}
