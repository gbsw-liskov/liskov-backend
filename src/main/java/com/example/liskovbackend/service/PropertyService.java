package com.example.liskovbackend.service;

import com.example.liskovbackend.dto.property.PropertyCreateRequest;
import com.example.liskovbackend.dto.property.PropertyDetail;
import com.example.liskovbackend.dto.property.PropertySummary;
import com.example.liskovbackend.entity.LeaseType;
import com.example.liskovbackend.entity.Property;
import com.example.liskovbackend.global.exception.ResourceAlreadyExistsException;
import com.example.liskovbackend.global.exception.ResourceNotFoundException;
import com.example.liskovbackend.repository.PropertyRepository;
import com.example.liskovbackend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PropertyService {

    private final PropertyRepository propertyRepository;
    private final UserRepository userRepository;

    @Transactional(readOnly = true)
    public List<PropertySummary> findAllProperties(Long userId) {
        return propertyRepository.findAllActiveByUserId(userId).stream()
            .map(PropertySummary::from)
            .toList();
    }

    @Transactional(readOnly = true)
    public PropertyDetail findPropertyById(Long id, Long userId) {
        var property = propertyRepository.findByIdAndUserId(id, userId)
            .orElseThrow(() -> new ResourceNotFoundException("매물을 찾을 수 없습니다."));
        return PropertyDetail.from(property);
    }

    @Transactional
    public void deleteProperty(Long id, Long userId) {
        var property = propertyRepository.findByIdAndUserId(id, userId)
            .orElseThrow(() -> new ResourceNotFoundException("매물을 찾을 수 없습니다."));
        property.delete();
        propertyRepository.save(property);
    }

    @Transactional
    public PropertyDetail saveProperty(PropertyCreateRequest request, Long userId) {
        var user = userRepository.findById(userId)
            .orElseThrow(() -> new ResourceNotFoundException("유저를 찾을 수 없습니다."));

        if (propertyRepository.existsByUserIdAndName(userId, request.getName())) {
            throw new ResourceAlreadyExistsException("이미 동일한 이름의 매물이 존재합니다.");
        }

        var property = Property.builder()
            .name(request.getName())
            .address(request.getAddress())
            .propertyType(request.getPropertyType())
            .floor(request.getFloor())
            .builtYear(request.getBuiltYear())
            .area(request.getArea())
            .deposit(request.getDeposit())
            .monthlyRent(request.getMonthlyRent())
            .marketPrice(request.getMarketPrice())
            .leaseType(LeaseType.valueOf(request.getLeaseType()))
            .memo(request.getMemo())
            .user(user)
            .build();

        var newProperty = propertyRepository.save(property);
        return PropertyDetail.from(newProperty);
    }
}
