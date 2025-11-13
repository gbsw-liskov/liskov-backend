package com.example.liskovbackend.service;

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
    private static final String CHECKLIST_ENDPOINT = "/checklist";

    public GptOssService(
            WebClient.Builder webClientBuilder,
            @Value("${gpt.oss.api-url}") String apiUrl,
            @Value("${gpt.oss.model-name}") String modelName) {

        this.apiUrl = apiUrl;
        this.modelName = modelName;
        // API URL을 기본 베이스로 설정하여 WebClient 인스턴스 생성
        this.webClient = webClientBuilder.baseUrl(apiUrl).build();
    }

    public Mono<ChecklistGenerateResponse> generateChecklist(GptChecklistGenerateRequest gptRequest) {

        return webClient.post()
                .uri(CHECKLIST_ENDPOINT) // "/checklist" 엔드포인트
                .body(Mono.just(gptRequest), GptChecklistGenerateRequest.class) // 요청 본문 설정
                .retrieve() // 응답 검색 시작
                // HTTP 상태 코드가 2xx가 아닌 경우 예외 처리
                .onStatus(
                        status -> status.is4xxClientError() || status.is5xxServerError(),
                        clientResponse -> Mono.error(new AiNoResponseException("AI를 호출에 실패하였습니다. : " + clientResponse.statusCode()))
                )
                .bodyToMono(ChecklistGenerateResponse.class);

    }
}
