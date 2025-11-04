package com.example.liskovbackend.dto.model.response;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class GptOssResponse {
    private List<Choice> choices;
}
