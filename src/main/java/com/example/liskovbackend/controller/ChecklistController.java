package com.example.liskovbackend.controller;

import com.example.liskovbackend.common.ApiResponse;
import com.example.liskovbackend.dto.ChecklistGetResponse;
import com.example.liskovbackend.dto.ChecklistSaveRequest;
import com.example.liskovbackend.dto.ChecklistSaveResponse;
import com.example.liskovbackend.service.ChecklistService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/checklist")
@RequiredArgsConstructor
public class ChecklistController {
    private final ChecklistService checklistService;
//    @PostMapping("/generate")
//    public ResponseEntity<ApiResponse<List<ChecklistGenerateResponse>>> generateChecklist(@RequestBody ChecklistGenerateRequest request){
//        List<ChecklistGenerateResponse> response = checklistService.generateChecklist(request);
//        return ApiResponse.ok(response);
//    }

    @PostMapping
    public ResponseEntity<ApiResponse<ChecklistSaveResponse>> saveChecklist(@Valid @RequestBody ChecklistSaveRequest request){
        ChecklistSaveResponse response = checklistService.saveChecklist(request);
        return ApiResponse.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ChecklistGetResponse>> getChecklistById(@PathVariable Long id){
        ChecklistGetResponse response = checklistService.getChecklistById(id);
        return ApiResponse.ok(response);
    }
}
