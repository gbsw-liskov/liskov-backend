package com.example.liskovbackend.service;

import com.example.liskovbackend.dto.gpt.request.GptSolutionGenerateRequest;
import com.example.liskovbackend.dto.risk.solution.response.SolutionDetailResponse;
import com.example.liskovbackend.dto.checklist.response.ChecklistGenerateResponse;
import com.example.liskovbackend.dto.gpt.request.GptChecklistGenerateRequest;
import com.example.liskovbackend.global.exception.AiNoResponseException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Service
public class GptOssService {
    private final WebClient webClient;
    private final String apiUrl;
    private final String modelName;
    private static final String RISK_SOLUTION_ENDPOINT = "/solution";
    private static final String CHECKLIST_ENDPOINT = "/checklist";

    public GptOssService(
            WebClient.Builder webClientBuilder,
            @Value("${gpt.oss.api-url}") String apiUrl,
            @Value("${gpt.oss.model-name}") String modelName) {

        this.apiUrl = apiUrl;
        this.modelName = modelName;
        this.webClient = webClientBuilder.baseUrl(apiUrl).build();
    }

    public Mono<ChecklistGenerateResponse> generateChecklist(GptChecklistGenerateRequest gptRequest) {

        return webClient.post()
                .uri(CHECKLIST_ENDPOINT)
                .body(Mono.just(gptRequest), GptChecklistGenerateRequest.class)
                .retrieve()
                .onStatus(
                        status -> status.is4xxClientError() || status.is5xxServerError(),
                        clientResponse -> Mono.error(new AiNoResponseException("AI를 호출에 실패하였습니다. : " + clientResponse.statusCode()))
                )
                .bodyToMono(ChecklistGenerateResponse.class);

    }

    public Mono<SolutionDetailResponse> generateSolution(GptSolutionGenerateRequest gptRequest) {

        return webClient.post()
                .uri(RISK_SOLUTION_ENDPOINT)
                .body(Mono.just(gptRequest), GptSolutionGenerateRequest.class)
                .retrieve()
                .onStatus(
                        status -> status.is4xxClientError() || status.is5xxServerError(),
                        clientResponse -> Mono.error(new AiNoResponseException("AI를 호출에 실패하였습니다. : " + clientResponse.statusCode()))
                )
                .bodyToMono(SolutionDetailResponse.class);

    }
}
