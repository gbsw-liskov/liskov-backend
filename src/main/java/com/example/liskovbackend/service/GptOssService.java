package com.example.liskovbackend.service;

import com.example.liskovbackend.dto.model.Message;
import com.example.liskovbackend.dto.checklist.request.ChecklistGenerateRequest;
import com.example.liskovbackend.dto.checklist.response.ChecklistGenerateResponse;
import com.example.liskovbackend.dto.model.request.GptOssRequest;
import com.example.liskovbackend.dto.model.response.GptOssResponse;
import com.example.liskovbackend.entity.Property;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.List;

@Service
public class GptOssService {
    private final WebClient webClient;

    public GptOssService(
            WebClient.Builder webClientBuilder,
            @Value("${gpt-oss.api-url}") String apiUrl
    ){
        this.webClient = webClientBuilder
                .baseUrl(apiUrl)
                .defaultHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                .build();


    }

    public Mono<GptOssResponse> generateChecklist(Property property){
        // 매물 정보 포맷
        String propertyInfo = """
                매물 이름 : %s
                주소 : %s 
                매물 타입 : %s
                층 수 : %d
                준공년도 : %d
                평 수 : %f
                입주 가능 일자 : %tF
                """
                .formatted(
                        property.getName(),
                        property.getAddress(),
                        property.getPropertyType(),
                        property.getFloor(),
                        property.getBuildYear(),
                        property.getArea(),
                        property.getAvailableDate()
                );

        // 시스템 Message(요청)
        Message systemMessage = Message.builder()
                .role("system")
                .content("체크리스트 형식으로 여러개의 항목으로 답변, 구분자로 ;를 사용, 줄바꿈(\\n)이나 특수문자 사용금지")
                .build();

        // 유저 Message
        Message userMessage = Message.builder()
                .role("user")
                .content(propertyInfo)
                .build();

        // 요청 req body 생성
        GptOssRequest requestBody = GptOssRequest.builder()
                .model("openai/gpt-oss-20b")
                .messages(List.of(systemMessage, userMessage))
                .build();

        // WebClient를 사용한 API POST 요청 및 응답 처리
        return webClient.post()
                .contentType(MediaType.APPLICATION_JSON) // 요청 본문 타입 명시
                .bodyValue(requestBody) // 직렬화할 요청 객체
                .retrieve() // 응답 검색 시작
                .bodyToMono(GptOssResponse.class) // 응답 본문을 GptOssResponse 객체로 역직렬화
                .doOnError(e -> System.err.println("GPT-OSS API Error: " + e.getMessage())); // 에러 처리 예시

    }
}
