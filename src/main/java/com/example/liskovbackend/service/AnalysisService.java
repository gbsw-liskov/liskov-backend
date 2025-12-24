package com.example.liskovbackend.service;

import com.example.liskovbackend.dto.analysis.AiAnalyzeRequest;
import com.example.liskovbackend.dto.analysis.AiAnalyzeResponse;
import com.example.liskovbackend.dto.analysis.AnalyzeRequest;
import com.example.liskovbackend.dto.analysis.AnalyzeResponse;
import com.example.liskovbackend.entity.Analysis;
import com.example.liskovbackend.entity.AnalysisDetail;
import com.example.liskovbackend.entity.Property;
import com.example.liskovbackend.entity.Severity;
import com.example.liskovbackend.global.exception.AiNoResponseException;
import com.example.liskovbackend.global.exception.ExternalServiceException;
import com.example.liskovbackend.global.exception.ResourceNotFoundException;
import com.example.liskovbackend.repository.AnalysisRepository;
import com.example.liskovbackend.repository.PropertyRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
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

        var property = propertyRepository.findById(request.propertyId())
            .orElseThrow(() -> new ResourceNotFoundException("매물을 찾을 수 없습니다."));

        var aiRequest = makeAiRequest(property, request, files);

        var aiResponse = sendMultipartToAi(aiRequest, files);

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
        AiAnalyzeRequest aiRequest,
        List<MultipartFile> files
    ) {
        try {
            var objectMapper = new ObjectMapper();
            var json = objectMapper.writeValueAsString(aiRequest);

            var body = new LinkedMultiValueMap<String, Object>();

            // JSON part
            var jsonHeaders = new HttpHeaders();
            jsonHeaders.setContentType(MediaType.APPLICATION_JSON);
            body.add("data", new HttpEntity<>(json, jsonHeaders));

            // file parts
            for (MultipartFile file : files) {
                body.add(
                    "files",
                    new ByteArrayResource(file.getBytes()) {
                        @Override
                        public String getFilename() {
                            return file.getOriginalFilename();
                        }
                    }
                );
            }

            var headers = new HttpHeaders();
            headers.setContentType(MediaType.MULTIPART_FORM_DATA);

            HttpEntity<MultiValueMap<String, Object>> entity =
                new HttpEntity<>(body, headers);

            return restTemplate.postForObject(
                aiUrl + ANALYSIS_ENDPOINT,
                entity,
                AiAnalyzeResponse.class
            );

        } catch (Exception e) {
            throw new ExternalServiceException("AI 서버 요청 실패");
        }
    }


    private AiAnalyzeRequest makeAiRequest(Property p, AnalyzeRequest req, List<MultipartFile> files) {

        var fileData = files.stream()
            .map(this::convertFile)
            .collect(Collectors.toCollection(ArrayList::new));

        return new AiAnalyzeRequest(
            p.getId(),
            p.getName(),
            p.getAddress(),
            p.getPropertyType().name(),
            p.getFloor(),
            p.getBuiltYear(),
            p.getArea().doubleValue(),
            req.marketPrice(),
            req.deposit(),
            req.monthlyRent(),
            fileData
        );
    }

    private AiAnalyzeRequest.FileData convertFile(MultipartFile file) {
        try {
            var encoded = Base64.getEncoder().encodeToString(file.getBytes());

            return new AiAnalyzeRequest.FileData(
                file.getOriginalFilename(),
                file.getContentType(),
                encoded
            );
        } catch (IOException e) {
            throw new ExternalServiceException("파일 인코딩이 실패했습니다.");
        }
    }

    private Analysis saveAnalysis(Property p, AiAnalyzeResponse ai) {

        var analysis = Analysis.builder()
            .property(p)
            .totalRisk(ai.totalRisk())
            .summary(ai.summary()) // 저장됨
            .build();

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
                .map(d -> new AnalyzeResponse.Detail(d.getOriginal(), d.getAnalysisText()))
                .toList(),
            a.getSummary()
        );
    }
}
