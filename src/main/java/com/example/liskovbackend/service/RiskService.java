package com.example.liskovbackend.service;

import com.example.liskovbackend.dto.risk.solution.request.SolutionGenerateRequest;
import com.example.liskovbackend.dto.risk.solution.response.CopingDto;
import com.example.liskovbackend.dto.risk.solution.response.SolutionDetailResponse;
import com.example.liskovbackend.dto.risk.solution.response.SolutionGenerateResponse;
import com.example.liskovbackend.entity.Coping;
import com.example.liskovbackend.entity.Property;
import com.example.liskovbackend.entity.Risk;
import com.example.liskovbackend.global.exception.ResourceAlreadyExistsException;
import com.example.liskovbackend.global.exception.ResourceNotFoundException;
import com.example.liskovbackend.repository.PropertyRepository;
import com.example.liskovbackend.repository.RiskRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RiskService {
    private final RiskRepository riskRepository;
    private final PropertyRepository propertyRepository;
    private final GptOssService gptOssService;

    @Transactional
    public SolutionGenerateResponse generateSolution(SolutionGenerateRequest request) {
        //매물이 존재하는지
        Property property = propertyRepository.findById(request.getPropertyId())
                .orElseThrow(() -> new ResourceNotFoundException("매물이 존재하지 않습니다."));

        Risk existingRisk = riskRepository.findByProperty(property)
                .orElseThrow(() -> new ResourceNotFoundException("위험 매물 분석 리포트가 존재하지 않습니다."));

        //위험 매물 리포트가 이미 존재할 경우 예외처리
        if(existingRisk.getCoping() != null && existingRisk.getChecklist() != null){
            throw new ResourceAlreadyExistsException("대처 방안이 이미 생성되었습니다.");
        }

        //존재하지 않는다면 생성
        //GptOssService를 통해 solution generate
        SolutionGenerateResponse response = gptOssService.generateSolution(request.getPropertyId());

        List<Coping> savedCopings = response.getCoping().stream()
                .map(copingResponse -> Coping.builder()
                        .title(copingResponse.getTitle())
                        .list(copingResponse.getList())
                        .risk(existingRisk)
                        .build())
                .toList();

        existingRisk.updateSolution(savedCopings, response.getChecklist());

        //SolutionGenerateResponse 반환
        return SolutionGenerateResponse.builder()
                .coping(response.getCoping())
                .checklist(response.getChecklist())
                .build();

    }

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
