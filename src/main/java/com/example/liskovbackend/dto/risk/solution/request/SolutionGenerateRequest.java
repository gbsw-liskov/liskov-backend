package com.example.liskovbackend.dto.risk.solution.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Data
public class SolutionGenerateRequest {
    @NotNull
    private Long propertyId;

    private Integer marketPrice;
    private Integer deposit;
    private Integer monthlyRent;

    private List<MultipartFile> files;
}
