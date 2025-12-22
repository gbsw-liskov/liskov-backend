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
                .bodyValue(gptRequest)
                .exchangeToMono(response -> {
                    if (response.statusCode().is2xxSuccessful()) {
                        return response.bodyToMono(ChecklistGenerateResponse.class)
                                .switchIfEmpty(Mono.error(
                                        new AiNoResponseException("AI 응답 바디가 비어 있습니다.")
                                ));
                    }
                    return response.bodyToMono(String.class)
                            .defaultIfEmpty("(응답 바디 없음)")
                            .flatMap(body ->
                                    Mono.error(new AiNoResponseException(
                                            "AI 호출 실패 (" + response.statusCode() + ") : " + body
                                    ))
                            );
                });

    }


    public Mono<SolutionDetailResponse> generateSolution(GptSolutionGenerateRequest gptRequest) {

        return webClient.post()
            .uri(RISK_SOLUTION_ENDPOINT)
            .bodyValue(gptRequest)
            .retrieve()
            .onStatus(
                status -> status.is4xxClientError() || status.is5xxServerError(),
                    clientResponse -> clientResponse.bodyToMono(String.class)
                            .defaultIfEmpty("(응답 바디 없음)")
                            .flatMap(errorBody -> {
                                var message = "AI 호출 실패 ("
                                        + clientResponse.statusCode() + ") : "
                                        + errorBody;

                                return Mono.error(new AiNoResponseException(message));
                            })
            )
            .bodyToMono(SolutionDetailResponse.class);
    }

    public Mono<LoanResponse> generateLoanGuide(LoanRequest loanRequest) {
        return webClient.post()
            .uri(LOAN_ENDPOINT)
            .bodyValue(loanRequest)
            .retrieve()
            .onStatus(
                status -> status.is4xxClientError() || status.is5xxServerError(),
                    clientResponse -> clientResponse.bodyToMono(String.class)
                            .defaultIfEmpty("(응답 바디 없음)")
                            .flatMap(errorBody -> {
                                var message = "AI 호출 실패 ("
                                        + clientResponse.statusCode() + ") : "
                                        + errorBody;

                                return Mono.error(new AiNoResponseException(message));
                            })
            )
            .bodyToMono(LoanResponse.class);
    }
}
