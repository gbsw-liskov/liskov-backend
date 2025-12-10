package com.example.liskovbackend.service;

import com.example.liskovbackend.dto.gpt.request.DetailDto;
import com.example.liskovbackend.dto.gpt.request.GptSolutionGenerateRequest;
import com.example.liskovbackend.dto.risk.solution.request.SolutionGenerateRequest;
import com.example.liskovbackend.dto.risk.solution.response.CopingDto;
import com.example.liskovbackend.dto.risk.solution.response.SolutionDetailResponse;
import com.example.liskovbackend.entity.*;
import com.example.liskovbackend.global.exception.AiNoResponseException;
import com.example.liskovbackend.global.exception.ResourceAlreadyExistsException;
import com.example.liskovbackend.global.exception.ResourceNotFoundException;
import com.example.liskovbackend.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SolutionService {

    private final SolutionRepository solutionRepository;
    private final PropertyRepository propertyRepository;
    private final AnalysisRepository analysisRepository;
    private final UserRepository userRepository;
    private final GptOssService gptOssService;
    private final SolutionCopingRepository solutionCopingRepository;

    @Transactional
    public SolutionDetailResponse generateSolution(SolutionGenerateRequest request, Long userId) {
        var property = propertyRepository.findByIdAndUserId(request.getPropertyId(), userId)
            .orElseThrow(() -> new ResourceNotFoundException("매물이 존재하지 않습니다."));

        var user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("유저가 존재하지 않습니다."));

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
            .buildYear(property.getBuildYear())
            .area(property.getArea())
            .availableDate(property.getAvailableDate())

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

        var savedSolution = Solution.builder()
                .property(property)
                .copings(null)
                .checklist(response.block().getChecklist())
                .build();

        List<SolutionCoping> savedSolutionCopings = response.block().getCoping().stream()
                .map(coping -> SolutionCoping.builder()
                        .title(coping.getTitle())
                        .list(coping.getList())
                        .build()
                ).collect(Collectors.toList());

        solutionCopingRepository.saveAll(savedSolutionCopings);

        savedSolution.updateCopings(savedSolutionCopings);
        solutionRepository.save(savedSolution);

        return response.block();
    }

    @Transactional(readOnly = true)
    public SolutionDetailResponse getSolution(Long id, Long userId) {
        var solution = solutionRepository.findByIdAndUserId(id, userId)
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
