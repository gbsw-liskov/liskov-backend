package com.example.liskovbackend.dto.property;

import com.example.liskovbackend.entity.PropertyType;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PropertyCreateRequestDto {
    private String name;
    private String address;
    private PropertyType propertyType;
    private Integer floor;
    private Integer builtYear;
    private BigDecimal area;
    private LocalDate availableDate;
}
