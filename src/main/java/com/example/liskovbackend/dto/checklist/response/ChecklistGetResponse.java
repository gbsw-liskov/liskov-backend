package com.example.liskovbackend.dto.checklist.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Builder
@AllArgsConstructor
public class ChecklistGetResponse {

    private Long checklistId;
    private Long propertyId;
    private List<ChecklistItemGetResponse> items;

}
