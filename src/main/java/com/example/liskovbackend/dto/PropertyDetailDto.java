package com.example.liskovbackend.dto;

import com.example.liskovbackend.entity.Property;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PropertyDetailDto {

    private Long propertyId;
    private String name;
    private String address;
    private String propertyType;
    private Integer floor;
    private Integer buildYear;
    private BigDecimal area;
    private LocalDate availableDate;
    private LocalDateTime createdAt;

    public static PropertyDetailDto from(Property property) {
        return PropertyDetailDto.builder()
            .propertyId(property.getId())
            .name(property.getName())
            .address(property.getAddress())
            .propertyType(property.getPropertyType().name())
            .floor(property.getFloor())
            .buildYear(property.getBuildYear())
            .area(property.getArea())
            .availableDate(property.getAvailableDate())
            .createdAt(property.getCreatedAt())
            .build();
    }
}
