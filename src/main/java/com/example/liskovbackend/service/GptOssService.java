package com.example.liskovbackend.service;

import com.example.liskovbackend.dto.gpt.request.GptSolutionGenerateRequest;
import com.example.liskovbackend.dto.loan.request.LoanRequest;
import com.example.liskovbackend.dto.loan.response.LoanResponse;
import com.example.liskovbackend.dto.risk.solution.response.SolutionDetailResponse;
import com.example.liskovbackend.dto.checklist.response.ChecklistGenerateResponse;
import com.example.liskovbackend.dto.gpt.request.GptChecklistGenerateRequest;
import com.example.liskovbackend.global.exception.AiNoResponseException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Service
public class GptOssService {
    private final WebClient webClient;
    private static final String RISK_SOLUTION_ENDPOINT = "/solution";
    private static final String CHECKLIST_ENDPOINT = "/checklist";
    private static final String LOAN_ENDPOINT = "/loan";

    public GptOssService(
        WebClient.Builder webClientBuilder,
        @Value("${ai.url}") String apiUrl) {

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

    public Mono<LoanResponse> generateLoanGuide(LoanRequest loanRequest) {
        return webClient.post()
            .uri(LOAN_ENDPOINT)
            .body(Mono.just(loanRequest), LoanRequest.class)
            .retrieve()
            .onStatus(
                status -> status.is4xxClientError() || status.is5xxServerError(),
                clientResponse -> Mono.error(new AiNoResponseException("AI를 호출에 실패하였습니다. : " + clientResponse.statusCode()))
            )
            .bodyToMono(LoanResponse.class);
    }
}
