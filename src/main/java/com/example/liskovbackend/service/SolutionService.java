package com.example.liskovbackend.service;

import com.example.liskovbackend.dto.gpt.request.DetailDto;
import com.example.liskovbackend.dto.gpt.request.GptSolutionGenerateRequest;
import com.example.liskovbackend.dto.risk.solution.request.SolutionGenerateRequest;
import com.example.liskovbackend.dto.risk.solution.response.CopingDto;
import com.example.liskovbackend.dto.risk.solution.response.SolutionDetailResponse;
import com.example.liskovbackend.entity.Analysis;
import com.example.liskovbackend.entity.Property;
import com.example.liskovbackend.entity.Solution;
import com.example.liskovbackend.global.exception.AiNoResponseException;
import com.example.liskovbackend.global.exception.ResourceAlreadyExistsException;
import com.example.liskovbackend.global.exception.ResourceNotFoundException;
import com.example.liskovbackend.repository.AnalysisRepository;
import com.example.liskovbackend.repository.PropertyRepository;
import com.example.liskovbackend.repository.SolutionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SolutionService {

    private final SolutionRepository solutionRepository;
    private final PropertyRepository propertyRepository;
    private final AnalysisRepository analysisRepository;
    private final GptOssService gptOssService;

    @Transactional
    public SolutionDetailResponse generateSolution(SolutionGenerateRequest request) {
        var property = propertyRepository.findById(request.getPropertyId())
            .orElseThrow(() -> new ResourceNotFoundException("매물이 존재하지 않습니다."));

        var solution = solutionRepository.findByProperty(property);

        if (solution != null) {
            throw new ResourceAlreadyExistsException("대처방안이 이미 생성되었습니다.");
        }

        var analysis = analysisRepository.findByProperty(property)
            .orElseThrow(() -> new ResourceNotFoundException("위험 매물 분석 결과가 존재하지 않습니다."));

        var detailDto = analysis.getDetails().stream()
            .map(detail -> DetailDto.builder()
                .title(detail.getOriginal())
                .content(detail.getAnalysisText())
                .build()
            )
            .toList();

        var gptRequest = GptSolutionGenerateRequest.builder()
            .propertyId(property.getId())
            .name(property.getName())
            .address(property.getAddress())
            .propertyType(property.getPropertyType())
            .floor(property.getFloor())
            .builtYear(property.getBuiltYear())
            .area(property.getArea())
            .marketPrice(request.getMarketPrice())
            .deposit(request.getDeposit())
            .monthlyRent(request.getMonthlyRent())
            .totalRisk(analysis.getTotalRisk())
            .summary(analysis.getSummary())
            .details(detailDto)
            .build();

        var response = gptOssService.generateSolution(gptRequest);
        if (response == null || response.block() == null) {
            throw new AiNoResponseException("대처방안이 생성되지 않았습니다.");
        }

        return response.block();
    }

    @Transactional(readOnly = true)
    public SolutionDetailResponse getSolution(Long id) {
        var solution = solutionRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("위험 매물 리포트를 찾을 수 없습니다."));

        return SolutionDetailResponse.builder()
            .coping(
                solution.getCopings().stream()
                    .map(coping -> CopingDto.builder()
                        .title(coping.getTitle())
                        .list(coping.getList())
                        .build())
                    .toList()
            )
            .checklist(solution.getChecklist())
            .build();
    }
}
