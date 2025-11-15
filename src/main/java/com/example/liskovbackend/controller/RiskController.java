package com.example.liskovbackend.controller;

import com.example.liskovbackend.common.ApiResponse;
import com.example.liskovbackend.dto.risk.solution.request.SolutionGenerateRequest;
import com.example.liskovbackend.dto.risk.solution.response.SolutionDetailResponse;
import com.example.liskovbackend.service.RiskService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/risk/solution")
public class RiskController {
    private final RiskService riskService;

    @PostMapping
    public ResponseEntity<ApiResponse<SolutionDetailResponse>> generateSolution(@Valid @RequestPart("request") SolutionGenerateRequest request){
        return ApiResponse.ok(riskService.generateSolution(request));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<SolutionDetailResponse>> getSolution(@PathVariable Long id){
        return ApiResponse.ok(riskService.getSolution(id));
    }
}
