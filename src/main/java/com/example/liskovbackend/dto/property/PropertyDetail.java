package com.example.liskovbackend.dto.property;

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
public class PropertyDetail {

    private Long propertyId;
    private String name;
    private String address;
    private String propertyType;
    private Integer floor;
    private Integer builtYear;
    private BigDecimal area;
    private Integer deposit;
    private Integer monthlyRent;
    private LocalDateTime createdAt;

    public static PropertyDetail from(Property property) {
        return PropertyDetail.builder()
            .propertyId(property.getId())
            .name(property.getName())
            .address(property.getAddress())
            .propertyType(property.getPropertyType().name())
            .floor(property.getFloor())
            .builtYear(property.getBuiltYear())
            .area(property.getArea())
            .deposit(property.getDeposit())
            .monthlyRent(property.getMonthlyRent())
            .createdAt(property.getCreatedAt())
            .build();
    }
}
