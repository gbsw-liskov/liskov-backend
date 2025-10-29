package com.example.liskovbackend.dto;

import com.example.liskovbackend.entity.Property;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter @Setter
@Builder
@AllArgsConstructor
public class PropertySummaryDto {

    private Long propertyId;
    private String name;
    private String address;
    private LocalDateTime createdAt;

    public static PropertySummaryDto from(Property property) {
        return PropertySummaryDto.builder()
            .propertyId(property.getId())
            .name(property.getName())
            .address(property.getName())
            .createdAt(property.getCreatedAt())
            .build();
    }
}
