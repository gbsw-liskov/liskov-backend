package com.example.liskovbackend.controller;

import com.example.liskovbackend.common.util.ApiResponse;
import com.example.liskovbackend.common.util.UserUtils;
import com.example.liskovbackend.dto.risk.solution.request.SolutionGenerateRequest;
import com.example.liskovbackend.dto.risk.solution.response.SolutionDetailResponse;
import com.example.liskovbackend.service.SolutionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/risk/solution")
public class SolutionController {

    private final SolutionService solutionService;
    private final UserUtils userUtils;

    // 대처 방안 생성
    @PostMapping
    public ResponseEntity<ApiResponse<SolutionDetailResponse>> generateSolution(@Valid @RequestPart("request") SolutionGenerateRequest request) {
        var userId = userUtils.getCurrentUserId();
        return ApiResponse.ok(solutionService.generateSolution(request, userId));
    }

    // 대처 방안 조회
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<SolutionDetailResponse>> getSolution(@PathVariable Long id) {
        var userId = userUtils.getCurrentUserId();
        return ApiResponse.ok(solutionService.getSolution(id, userId));
    }
}
