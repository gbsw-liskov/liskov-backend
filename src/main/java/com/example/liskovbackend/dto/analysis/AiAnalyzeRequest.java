package com.example.liskovbackend.dto.analysis;

import java.util.List;

public record AiAnalyzeRequest(
    Long propertyId,
    String name,
    String address,
    String propertyType,
    Integer floor,
    Integer builtYear,
    Double area,

    Integer marketPrice,
    String leaseType,
    Integer deposit,
    Integer monthlyRent,

    List<FileData> files
) {
    public record FileData(
        String filename,
        String contentType,
        String content
    ) {}
}
