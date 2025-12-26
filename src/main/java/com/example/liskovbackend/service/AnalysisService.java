package com.example.liskovbackend.service;

import com.example.liskovbackend.dto.analysis.*;
import com.example.liskovbackend.entity.*;
import com.example.liskovbackend.global.exception.*;
import com.example.liskovbackend.global.util.MultipartInputStreamFileResource;
import com.example.liskovbackend.repository.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class AnalysisService {

    private final PropertyRepository propertyRepository;
    private final AnalysisRepository analysisRepository;
    private final RestTemplate restTemplate;

    private final String ANALYSIS_ENDPOINT = "/analyze";

    @Value("${ai.url}")
    private String aiUrl;

    public AnalyzeResponse analyze(AnalyzeRequest request, List<MultipartFile> files) {
        if (analysisRepository.existsByPropertyId(request.propertyId())) {
            throw new ResourceAlreadyExistsException("이미 분석된 매물입니다.");
        }

        if (files == null || files.isEmpty()) {
            throw new IllegalArgumentException("파일은 최소 1개 이상 필요합니다.");
        }

        var property = propertyRepository.findById(request.propertyId())
            .orElseThrow(() -> new ResourceNotFoundException("매물을 찾을 수 없습니다."));

        var aiResponse = sendMultipartToAi(property, request, files);

        if (aiResponse == null) {
            throw new AiNoResponseException("AI 서버가 응답할 수 없습니다.");
        }

        var analysis = saveAnalysis(property, aiResponse);
        return convertToResponse(analysis);
    }


    @Transactional(readOnly = true)
    public AnalyzeResponse getAnalysis(Long id) {
        var analysis = analysisRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("분석 결과를 찾을 수 없습니다."));
        return convertToResponse(analysis);
    }

    private AiAnalyzeResponse sendMultipartToAi(
        Property p,
        AnalyzeRequest req,
        List<MultipartFile> files
    ) {
        try {
            log.info("[AI REQUEST] POST {}{}", aiUrl, ANALYSIS_ENDPOINT);

            var headers = new HttpHeaders();
            headers.setContentType(MediaType.MULTIPART_FORM_DATA);

            var body = new LinkedMultiValueMap<String, Object>();

            for (MultipartFile file : files) {
                body.add("files", new MultipartInputStreamFileResource(file));
            }

            body.add("propertyId", p.getId().toString());
            body.add("name", p.getName());
            body.add("address", p.getAddress());
            body.add("propertyType", p.getPropertyType().name());
            body.add("floor", p.getFloor().toString());
            body.add("builtYear", p.getBuiltYear().toString());
            body.add("area", p.getArea().toString());
            body.add("marketPrice", p.getMarketPrice().toString());
            body.add("deposit", p.getDeposit().toString());
            body.add("monthlyRent", p.getMonthlyRent().toString());

            HttpEntity<MultiValueMap<String, Object>> requestEntity =
                new HttpEntity<>(body, headers);

            var response = restTemplate.exchange(
                aiUrl + ANALYSIS_ENDPOINT,
                HttpMethod.POST,
                requestEntity,
                AiAnalyzeResponse.class
            );

            log.info("[AI RESPONSE RAW] {}", response.getBody());
            return response.getBody();

        } catch (Exception e) {
            log.error("[AI REQUEST FAILED]", e);
            throw new ExternalServiceException("AI 서버 요청 실패");
        }
    }

    private Analysis saveAnalysis(Property p, AiAnalyzeResponse ai) {

        var analysis = Analysis.builder()
            .property(p)
            .totalRisk(ai.totalRisk())
            .summary(ai.summary())
            .build();

        if (ai.details() == null || ai.details().isEmpty()) {
            log.warn("[AI RESPONSE] details is null or empty. propertyId={}", p.getId());
            return analysisRepository.save(analysis);
        }

        var details = ai.details().stream()
            .map(d -> AnalysisDetail.builder()
                .analysis(analysis)
                .original(d.title())
                .analysisText(d.content())
                .severity(Severity.valueOf(d.severity()))
                .build())
            .toList();

        analysis.updateDetails(details);
        return analysisRepository.save(analysis);
    }

    private AnalyzeResponse convertToResponse(Analysis a) {
        return new AnalyzeResponse(
            a.getTotalRisk(),
            a.getDetails().stream()
                .map(d -> new AnalyzeResponse.Detail(
                    d.getOriginal(),
                    d.getAnalysisText()
                ))
                .toList(),
            a.getSummary()
        );
    }
}
