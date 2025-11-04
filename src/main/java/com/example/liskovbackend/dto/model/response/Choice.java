package com.example.liskovbackend.dto.model.response;

import com.example.liskovbackend.dto.model.Message;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@Builder
public class Choice {
    private Integer index;
    private Message message;
}
