package com.example.liskovbackend.dto.gpt.request;

import com.example.liskovbackend.entity.PropertyType;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Data
@Builder
public class GptSolutionGenerateRequest {
    private Long propertyId;
    private String name;
    private String address;
    private PropertyType propertyType;
    private Integer floor;
    private Integer buildYear;
    private BigDecimal area;
    private LocalDate availableDate;
    private Integer marketPrice;
    private Integer deposit;
    private Integer monthlyRent;
    private Integer totalRisk;
    private String summary;
    private List<DetailDto> details;

}
