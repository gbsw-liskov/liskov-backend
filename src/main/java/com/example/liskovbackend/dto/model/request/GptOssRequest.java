package com.example.liskovbackend.dto.model.request;

import com.example.liskovbackend.dto.model.Message;
import lombok.*;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@Builder
public class GptOssRequest {
    private List<Message> messages;
    private String model;
}
