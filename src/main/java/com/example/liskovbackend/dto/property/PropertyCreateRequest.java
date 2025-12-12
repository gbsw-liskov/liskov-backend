package com.example.liskovbackend.dto.property;

import com.example.liskovbackend.entity.PropertyType;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PropertyCreateRequest {
    private String name;
    private String address;
    private PropertyType propertyType;
    private Integer floor;
    private Integer builtYear;
    private BigDecimal area;
    private Integer marketPrice;
    private String leaseType;
    private Integer deposit;
    private Integer monthlyRent;
    private String memo;
}
