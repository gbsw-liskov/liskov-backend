package com.example.liskovbackend.dto.checklist.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@Builder
public class AllChecklistGetResponse {
    //[{
    //    checklistId,
    //    propertyId,
    //    createdAt,
    //    itemCount,
    //}]
    private Long checklistId;
    private Long propertyId;
    private LocalDateTime createdAt;
    private Integer itemCount;
}
