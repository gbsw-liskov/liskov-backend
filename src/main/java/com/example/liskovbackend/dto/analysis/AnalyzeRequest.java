package com.example.liskovbackend.dto.analysis;

import lombok.Data;

public record AnalyzeRequest(
    Long propertyId,
    Double marketPrice,
    Double deposit,
    Double monthlyRent
) {}
