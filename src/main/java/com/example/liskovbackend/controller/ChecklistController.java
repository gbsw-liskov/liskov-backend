package com.example.liskovbackend.controller;

import com.example.liskovbackend.common.ApiResponse;
import com.example.liskovbackend.dto.*;
import com.example.liskovbackend.service.ChecklistService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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

    //체크리스트 저장
    @PostMapping
    public ResponseEntity<ApiResponse<ChecklistSaveResponse>> saveChecklist(@Valid @RequestBody ChecklistSaveRequest request){
        ChecklistSaveResponse response = checklistService.saveChecklist(request);
        return ApiResponse.ok(response);
    }

    //체크리스트 개별 조회
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ChecklistGetResponse>> getChecklistById(@PathVariable Long id){
        ChecklistGetResponse response = checklistService.getChecklistById(id);
        return ApiResponse.ok(response);
    }

    //체크리스트 전체 조회
    @GetMapping
    public ResponseEntity<ApiResponse<List<AllChecklistGetResponse>>> getAllChecklist(){
        List<AllChecklistGetResponse> response = checklistService.getAllChecklist();
        return ApiResponse.ok(response);
    }

    //체크리스트 삭제
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteChecklist(@PathVariable Long id){
        checklistService.deleteChecklist(id);
        return ApiResponse.noContent();
    }

    //체크리스트 수정
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<ChecklistUpdateResponse>> updateChecklist(@PathVariable Long id, @Valid @RequestBody List<ChecklistUpdateRequest> request){
        ChecklistUpdateResponse response = checklistService.updateChecklist(id, request);
        return ApiResponse.ok(response);
    }
}
