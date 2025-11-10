package com.example.liskovbackend.service;

import com.example.liskovbackend.dto.risk.solution.request.SolutionGenerateRequest;
import com.example.liskovbackend.dto.risk.solution.response.SolutionGenerateResponse;
import com.example.liskovbackend.global.exception.ExternalServiceException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

@Service
public class GptOssService {

    private final WebClient webClient;

    public GptOssService(WebClient.Builder webClientBuilder, @Value("${gpt-oss.api-url}") String gptOssApiUrl) {
        this.webClient = webClientBuilder.baseUrl(gptOssApiUrl).build();
    }

    public SolutionGenerateResponse generateSolution(Long propertyId) {

        try {
            SolutionGenerateResponse response = webClient.post()
                    .uri("/solution")
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(propertyId)
                    .retrieve()
                    .onStatus(HttpStatusCode::isError, clientResponse ->
                            clientResponse.bodyToMono(String.class)
                                    .flatMap(errorBody -> {
                                        throw new ExternalServiceException("대처 방안을 생성하는 중에 오류가 발생하였습니다. :" + errorBody);
                                    })
                    )
                    .bodyToMono(SolutionGenerateResponse.class)
                    .block();

            if (response == null) {
                throw new ExternalServiceException("AI가 생성한 대처 방안이 null입니다.");
            }

            return response;

        } catch (Exception e) {
            // Exception 처리는 기존 로직 유지
            throw new ExternalServiceException("대처 방안을 생성하는 중 오류가 발생하였습니다. : " + propertyId);
        }
    }
}