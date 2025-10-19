package com.example.liskovbackend.service;

import com.example.liskovbackend.dto.PropertyCreateRequestDto;
import com.example.liskovbackend.dto.PropertyDetailDto;
import com.example.liskovbackend.dto.PropertySummaryDto;
import com.example.liskovbackend.entity.Property;
import com.example.liskovbackend.exception.ResourceNotFoundException;
import com.example.liskovbackend.repository.PropertyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PropertyService {

    private final PropertyRepository propertyRepository;

    @Transactional(readOnly = true)
    public List<PropertySummaryDto> findAllProperties() {
        return propertyRepository.findAllActive().stream()
            .map(property -> PropertySummaryDto.builder()
                .propertyId(property.getId())
                .name(property.getName())
                .address(property.getAddress())
                .build())
            .toList();
    }

    @Transactional(readOnly = true)
    public PropertyDetailDto findPropertyById(Long id) {
        var property = propertyRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Property not found with id: " + id));

        return PropertyDetailDto.from(property);
    }

    @Transactional
    public void deleteProperty(Long id) {
        Property property = propertyRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Property not found with id: " + id));

        property.setIsDeleted(true);
        propertyRepository.save(property);
    }

    @Transactional
    public PropertyDetailDto saveProperty(PropertyCreateRequestDto request) {
        var property = Property.builder()
            .name(request.getName())
            .address(request.getAddress())
            .propertyType(request.getPropertyType())
            .floor(request.getFloor())
            .buildYear(request.getBuiltYear())
            .area(request.getArea())
            .availableDate(request.getAvailableDate())
            .build();

        var newProperty = propertyRepository.save(property);
        return PropertyDetailDto.from(newProperty);
    }
}
