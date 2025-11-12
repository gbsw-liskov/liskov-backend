package com.example.liskovbackend.controller;

import com.example.liskovbackend.common.ApiResponse;
import com.example.liskovbackend.dto.risk.solution.response.SolutionDetailResponse;
import com.example.liskovbackend.service.RiskService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/risk/solution")
public class RiskController {
    private final RiskService riskService;

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<SolutionDetailResponse>> getSolution(@PathVariable Long id){
        SolutionDetailResponse response = riskService.getSolution(id);
        return ApiResponse.ok(response);
    }
}
