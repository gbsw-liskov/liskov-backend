package com.example.liskovbackend.controller;

import com.example.liskovbackend.common.ApiResponse;
import com.example.liskovbackend.dto.property.PropertyCreateRequestDto;
import com.example.liskovbackend.dto.property.PropertyDetailDto;
import com.example.liskovbackend.dto.property.PropertySummaryDto;
import com.example.liskovbackend.service.PropertyService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/properties")
@RequiredArgsConstructor
public class PropertyController {

    private final PropertyService propertyService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<PropertySummaryDto>>> getAllProperties() {
        return ApiResponse.ok(propertyService.findAllProperties());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<PropertyDetailDto>> getPropertyDetail(@PathVariable Long id) {
        return ApiResponse.ok(propertyService.findPropertyById(id));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<PropertyDetailDto>> createProperty(@RequestBody PropertyCreateRequestDto request) {
        return ApiResponse.ok(propertyService.saveProperty(request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteProperty(@PathVariable Long id) {
        propertyService.deleteProperty(id);
        return ApiResponse.ok(null);
    }

}
