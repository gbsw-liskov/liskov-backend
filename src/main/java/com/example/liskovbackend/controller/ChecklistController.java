package com.example.liskovbackend.controller;

import com.example.liskovbackend.common.ApiResponse;
import com.example.liskovbackend.dto.checklist.request.ChecklistSaveRequest;
import com.example.liskovbackend.dto.checklist.request.ChecklistUpdateRequest;
import com.example.liskovbackend.dto.checklist.response.AllChecklistGetResponse;
import com.example.liskovbackend.dto.checklist.response.ChecklistGetResponse;
import com.example.liskovbackend.dto.checklist.response.ChecklistSaveResponse;
import com.example.liskovbackend.dto.checklist.response.ChecklistUpdateResponse;
import com.example.liskovbackend.service.ChecklistService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/checklist")
@RequiredArgsConstructor
public class ChecklistController {
    private final ChecklistService checklistService;

    @PostMapping("/generate")
    public ResponseEntity<ApiResponse<List<ChecklistGenerateResponse>>> generateChecklist(@RequestBody ChecklistGenerateRequest request){
        List<ChecklistGenerateResponse> response = checklistService.generateChecklist(request);
        return ApiResponse.ok(response);
    }

    //체크리스트 저장
    @PostMapping
    public ResponseEntity<ApiResponse<ChecklistSaveResponse>> saveChecklist(@Valid @RequestBody ChecklistSaveRequest request) {
        return ApiResponse.ok(checklistService.saveChecklist(request));
    }

    //체크리스트 개별 조회
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ChecklistGetResponse>> getChecklistById(@PathVariable Long id) {
        return ApiResponse.ok(checklistService.getChecklistById(id));
    }

    //체크리스트 전체 조회
    @GetMapping
    public ResponseEntity<ApiResponse<List<AllChecklistGetResponse>>> getAllChecklist() {
        return ApiResponse.ok(checklistService.getAllChecklist());
    }

    //체크리스트 삭제
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteChecklist(@PathVariable Long id){
        checklistService.deleteChecklist(id);
        return ApiResponse.ok("체크리스트가 삭제되었습니다.");
    }

    //체크리스트 수정
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<ChecklistUpdateResponse>> updateChecklist(@PathVariable Long id, @Valid @RequestBody List<ChecklistUpdateRequest> request) {
        return ApiResponse.ok(checklistService.updateChecklist(id, request));
    }
}
