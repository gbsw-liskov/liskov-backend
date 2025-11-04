package com.example.liskovbackend.dto.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@Builder
public class Message {
    private String content;
    private String role;

    public Message(String content) {
        this.content = content;
    }
}
