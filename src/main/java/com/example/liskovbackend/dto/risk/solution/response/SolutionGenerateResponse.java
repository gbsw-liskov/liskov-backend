package com.example.liskovbackend.dto.risk.solution.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
@Builder
public class SolutionGenerateResponse {
    //"{
    //    coping: [{
    //        title: string,
    //        list: [string],
    //    ]},
    //    checklist: [string]
    //}"
    private List<CopingDto> coping;
    private List<String> checklist;
}
