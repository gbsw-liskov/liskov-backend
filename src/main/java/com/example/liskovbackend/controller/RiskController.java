package com.example.liskovbackend.controller;

import com.example.liskovbackend.common.ApiResponse;
import com.example.liskovbackend.dto.risk.solution.request.SolutionGenerateRequest;
import com.example.liskovbackend.dto.risk.solution.response.SolutionDetailResponse;
import com.example.liskovbackend.dto.risk.solution.response.SolutionGenerateResponse;
import com.example.liskovbackend.service.RiskService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/risk/solution")
public class RiskController {
    private final RiskService riskService;

    @PostMapping
    public ResponseEntity<ApiResponse<SolutionGenerateResponse>> generateSolution(@Valid @RequestBody SolutionGenerateRequest request){
        SolutionGenerateResponse response = riskService.generateSolution(request);
        return ApiResponse.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<SolutionDetailResponse>> getSolution(@PathVariable Long id){
        SolutionDetailResponse response = riskService.getSolution(id);
        return ApiResponse.ok(response);
    }
}
