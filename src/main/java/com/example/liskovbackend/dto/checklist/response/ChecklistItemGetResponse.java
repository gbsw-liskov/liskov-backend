package com.example.liskovbackend.dto.checklist.response;

import com.example.liskovbackend.entity.Severity;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ChecklistItemGetResponse {

    private Long itemId;
    private String content;
    private Severity severity;
    private String memo;
}
