package com.example.liskovbackend.controller;

import com.example.liskovbackend.common.util.ApiResponse;
import com.example.liskovbackend.common.util.UserUtils;
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
    private final UserUtils userUtils;

    // 매물 분석
    @PostMapping
    public ResponseEntity<ApiResponse<AnalyzeResponse>> analyze(
        @RequestPart("request") AnalyzeRequest request,
        @RequestPart(value = "files", required = false) List<MultipartFile> files
    ) {
        var userId = userUtils.getCurrentUserId();
        var response = analysisService.analyze(request, files, userId);
        return ApiResponse.ok(response);
    }

    // 매물 분석 조회
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<AnalyzeResponse>> getAnalysis(@PathVariable Long id) {
        var userId = userUtils.getCurrentUserId();
        var response = analysisService.getAnalysis(id, userId);
        return ApiResponse.ok(response);
    }
}
