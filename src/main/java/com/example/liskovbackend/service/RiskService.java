package com.example.liskovbackend.service;

import com.example.liskovbackend.dto.gpt.request.DetailDto;
import com.example.liskovbackend.dto.gpt.request.GptSolutionGenerateRequest;
import com.example.liskovbackend.dto.risk.analyze.response.AnalyzeGenerateResponse;
import com.example.liskovbackend.dto.risk.solution.request.SolutionGenerateRequest;
import com.example.liskovbackend.dto.risk.solution.response.CopingDto;
import com.example.liskovbackend.dto.risk.solution.response.SolutionDetailResponse;
import com.example.liskovbackend.entity.Property;
import com.example.liskovbackend.entity.Risk;
import com.example.liskovbackend.global.exception.AiNoResponseException;
import com.example.liskovbackend.global.exception.ResourceAlreadyExistsException;
import com.example.liskovbackend.global.exception.ResourceNotFoundException;
import com.example.liskovbackend.repository.PropertyRepository;
import com.example.liskovbackend.repository.RiskRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RiskService {
    private final RiskRepository riskRepository;
    private final PropertyRepository propertyRepository;
    private final WebClient webClient;
    private final GptOssService gptOssService;

    //위험 매물 알림 - 분석 결과 불러오는 메서드
    public AnalyzeGenerateResponse callAnalyzeApi() {
        return webClient.get()
                .uri("/analyze")
                .retrieve()
                .bodyToMono(AnalyzeGenerateResponse.class) // 응답 JSON을 DTO 객체로 변환
                .block();
    }

    //대처방안 생성
    @Transactional
    public SolutionDetailResponse generateSolution(SolutionGenerateRequest request) {
        //매물 찾기
        Property property = propertyRepository.findById(request.getPropertyId())
                .orElseThrow(() -> new ResourceNotFoundException("매물이 존재하지 않습니다."));

        //risk 찾기
        Risk risk = riskRepository.findByProperty(property)
                .orElseThrow(() -> new ResourceNotFoundException("위험 매물 리포트가 생성되지 않았습니다. 분석을 요청해주세요"));

        //이미 risk에 대처방안이 생성되어있다면 예외처리
        if(!risk.getCoping().isEmpty() || !risk.getChecklist().isEmpty()){
            throw new ResourceAlreadyExistsException("위험 매물 리포트가 이미 생성되었습니다. 조회를 요청해주세요.");
        }

        AnalyzeGenerateResponse analyzeResult = callAnalyzeApi();

        List<DetailDto> detailRequest = analyzeResult.getDetails().stream()
                .map(detail -> new DetailDto(
                                detail.getTitle(),
                                detail.getContent()
                        )
                )
                .toList();


        //AI 요청 Dto 생성
        GptSolutionGenerateRequest gptRequest = GptSolutionGenerateRequest.builder()
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

                .totalRisk(analyzeResult.getTotalRisk())
                .summary(analyzeResult.getSummary())
                .details(detailRequest)
                .build();

        //GptOssService 호출
        Mono<SolutionDetailResponse> response = gptOssService.generateSolution(gptRequest);

        //응답 반환
        if(response == null || response.block() == null){
            throw new AiNoResponseException("대처방안이 생성되지 않았습니다.");
        }

        //응답 반환
        return response.block();
    }

    //대처 방안 조회
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
