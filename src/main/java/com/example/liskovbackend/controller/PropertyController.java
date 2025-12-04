package com.example.liskovbackend.controller;

import com.example.liskovbackend.dto.property.PropertyCreateRequestDto;
import com.example.liskovbackend.dto.property.PropertyDetailDto;
import com.example.liskovbackend.dto.property.PropertySummaryDto;
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

    private Long getCurrentUserId() {
        var auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || auth.getPrincipal() == null) {
            throw new RuntimeException("인증되지 않은 요청입니다.");
        }
        return Long.valueOf(auth.getPrincipal().toString());
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<PropertySummaryDto>>> getAllProperties() {
        var userId = getCurrentUserId();
        return ApiResponse.ok(propertyService.findAllProperties(userId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<PropertyDetailDto>> getPropertyDetail(@PathVariable Long id) {
        var userId = getCurrentUserId();
        return ApiResponse.ok(propertyService.findPropertyById(id, userId));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<PropertyDetailDto>> createProperty(@RequestBody PropertyCreateRequestDto request) {
        var userId = getCurrentUserId();
        return ApiResponse.ok(propertyService.saveProperty(request, userId));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteProperty(@PathVariable Long id) {
        var userId = getCurrentUserId();
        propertyService.deleteProperty(id, userId);
        return ApiResponse.ok(null);
    }
}
