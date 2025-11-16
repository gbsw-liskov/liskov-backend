package com.example.liskovbackend.dto.gpt.request;

import com.example.liskovbackend.entity.PropertyType;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Builder
public class GptChecklistGenerateRequest {
    private Long propertyId;
    private String name;
    private String address;
    private PropertyType propertyType;
    private Integer floor;
    private Integer buildYear;
    private BigDecimal area;
    private LocalDate availableDate;
}
