package com.example.liskovbackend.dto.property;

import com.example.liskovbackend.entity.Property;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter @Setter
@Builder
@AllArgsConstructor
public class PropertySummary {

    private Long propertyId;
    private String name;
    private String address;
    private String propertyType;
    private Integer floor;
    private Double area;
    private String leaseType;
    private Integer deposit;
    private Integer monthlyRent;
    private LocalDateTime createdAt;

    public static PropertySummary from(Property property) {
        return PropertySummary.builder()
            .propertyId(property.getId())
            .name(property.getName())
            .address(property.getAddress())
            .propertyType(property.getPropertyType().name())
            .floor(property.getFloor())
            .area(property.getArea().doubleValue())
            .leaseType(property.getLeaseType().toString())
            .deposit(property.getDeposit())
            .monthlyRent(property.getMonthlyRent())
            .createdAt(property.getCreatedAt())
            .build();
    }
}