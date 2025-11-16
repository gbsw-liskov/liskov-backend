package com.example.liskovbackend.service;

import com.example.liskovbackend.dto.analysis.AiAnalyzeRequest;
import com.example.liskovbackend.dto.analysis.AiAnalyzeResponse;
import com.example.liskovbackend.dto.analysis.AnalyzeRequest;
import com.example.liskovbackend.dto.analysis.AnalyzeResponse;
import com.example.liskovbackend.entity.Analysis;
import com.example.liskovbackend.entity.AnalysisDetail;
import com.example.liskovbackend.entity.Property;
import com.example.liskovbackend.entity.Severity;
import com.example.liskovbackend.global.exception.ResourceNotFoundException;
import com.example.liskovbackend.repository.AnalysisRepository;
import com.example.liskovbackend.repository.PropertyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

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

    public AnalyzeResponse analyze(AnalyzeRequest request, List<MultipartFile> files) {

        var property = propertyRepository.findById(request.propertyId())
            .orElseThrow(() -> new ResourceNotFoundException("Property not found"));

        var aiRequest = makeAiRequest(property, request, files);

        var aiResponse = restTemplate.postForObject(
            "http://ai.zipchak-backend.kro.kr:8080/analyze",
            aiRequest,
            AiAnalyzeResponse.class
        );

        if (aiResponse == null) {
            throw new IllegalStateException("AI 서버 응답 없음");
        }

        var analysis = saveAnalysis(property, aiResponse);

        return convertToResponse(analysis);
    }

    @Transactional(readOnly = true)
    public AnalyzeResponse getAnalysis(Long id) {
        var analysis = analysisRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Analysis not found"));

        return convertToResponse(analysis);
    }

    private AiAnalyzeRequest makeAiRequest(Property p, AnalyzeRequest req, List<MultipartFile> files) {

        List<AiAnalyzeRequest.FileData> fileData = files.stream()
            .map(this::convertFile)
            .collect(Collectors.toCollection(ArrayList::new));


        return new AiAnalyzeRequest(
            p.getId(),
            p.getName(),
            p.getAddress(),
            p.getPropertyType().name(),
            p.getFloor(),
            p.getBuildYear(),
            p.getArea().doubleValue(),
            p.getAvailableDate().toString(),
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
        } catch (Exception e) {
            throw new RuntimeException("파일 인코딩 실패");
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
                .title(d.title())
                .content(d.content())
                .severity(Severity.valueOf(d.severity()))
                .build())
            .toList();

        analysis.setDetails(details);

        return analysisRepository.save(analysis);
    }

    private AnalyzeResponse convertToResponse(Analysis a) {
        return new AnalyzeResponse(
            a.getTotalRisk(),
            a.getDetails().stream()
                .map(d -> new AnalyzeResponse.Detail(d.getTitle(), d.getContent()))
                .toList(),
            a.getSummary()
        );
    }
}
