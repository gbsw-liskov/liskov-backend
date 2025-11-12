package com.example.liskovbackend.service;

import com.example.liskovbackend.dto.risk.solution.response.CopingDto;
import com.example.liskovbackend.dto.risk.solution.response.SolutionDetailResponse;
import com.example.liskovbackend.entity.Risk;
import com.example.liskovbackend.global.exception.ResourceNotFoundException;
import com.example.liskovbackend.repository.RiskRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class RiskService {
    private final RiskRepository riskRepository;


    @Transactional(readOnly = true)
    public SolutionDetailResponse getSolution(Long id) {
        //find risk
        Risk risk = riskRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("위험 매물 리포트를 찾을 수 없습니다."));

        if(risk.getCoping().isEmpty() || risk.getChecklist().isEmpty()){
            throw new ResourceNotFoundException("리포트에 저장된 대처 방안이 존재하지 않습니다.");
        }

        //dto 반환
        return SolutionDetailResponse.builder()
                .coping(
                        risk.getCoping().stream()
                                .map(coping -> CopingDto.builder()
                                        .title(coping.getTitle())
                                        .list(coping.getList())
                                        .build())
                                .toList()
                )
                .checklist(risk.getChecklist())
                .build();

    }
}
