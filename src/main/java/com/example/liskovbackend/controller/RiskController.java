package com.example.liskovbackend.controller;

import com.example.liskovbackend.common.ApiResponse;
import com.example.liskovbackend.dto.risk.solution.request.SolutionGenerateRequest;
import com.example.liskovbackend.dto.risk.solution.response.SolutionGenerateResponse;
import com.example.liskovbackend.service.RiskService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
}
