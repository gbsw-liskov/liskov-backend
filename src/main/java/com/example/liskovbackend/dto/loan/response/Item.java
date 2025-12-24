package com.example.liskovbackend.dto.loan.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class Item {
    private String title;
    private String content;
}
