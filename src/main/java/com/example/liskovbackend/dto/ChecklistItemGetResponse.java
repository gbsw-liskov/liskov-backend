package com.example.liskovbackend.dto;

import com.example.liskovbackend.enums.Severity;
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
