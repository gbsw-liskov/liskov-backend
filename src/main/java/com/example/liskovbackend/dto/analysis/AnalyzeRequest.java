package com.example.liskovbackend.dto.analysis;

public record AnalyzeRequest(
    Long propertyId,
    Double marketPrice,
    Double deposit,
    Double monthlyRent
) {}
