package com.example.liskovbackend.controller;

import com.example.liskovbackend.common.util.ApiResponse;
import com.example.liskovbackend.dto.property.PropertyCreateRequest;
import com.example.liskovbackend.dto.property.PropertyDetail;
import com.example.liskovbackend.dto.property.PropertySummary;
import com.example.liskovbackend.service.PropertyService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
@RestController
@RequestMapping("/api/properties")
@RequiredArgsConstructor
public class PropertyController {

    private final PropertyService propertyService;

    // 사용자 아이디 불러오는 유틸
    private Long getCurrentUserId() {
        var auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || auth.getPrincipal() == null) {
            throw new RuntimeException("인증되지 않은 요청입니다.");
        }
        return Long.valueOf(auth.getPrincipal().toString());
    }

    // 매물 전체 조회
    @GetMapping
    public ResponseEntity<ApiResponse<List<PropertySummary>>> getAllProperties() {
        var userId = getCurrentUserId();
        return ApiResponse.ok(propertyService.findAllProperties(userId));
    }

    // 매물 단건 조회
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<PropertyDetail>> getPropertyDetail(@PathVariable Long id) {
        var userId = getCurrentUserId();
        return ApiResponse.ok(propertyService.findPropertyById(id, userId));
    }

    // 매물 생성
    @PostMapping
    public ResponseEntity<ApiResponse<PropertyDetail>> createProperty(@RequestBody PropertyCreateRequest request) {
        var userId = getCurrentUserId();
        return ApiResponse.ok(propertyService.saveProperty(request, userId));
    }

    // 매물 삭제
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteProperty(@PathVariable Long id) {
        var userId = getCurrentUserId();
        propertyService.deleteProperty(id, userId);
        return ApiResponse.ok(null);
    }
}
