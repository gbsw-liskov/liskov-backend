package com.example.liskovbackend.service.checklist;


import com.example.liskovbackend.dto.checklist.response.ChecklistGenerateResponse;
import com.example.liskovbackend.dto.gpt.request.GptChecklistGenerateRequest;
import com.example.liskovbackend.entity.PropertyType;
import com.example.liskovbackend.service.GptOssService;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

public class GptOssServiceTest {
    public static MockWebServer mockWebServer;
    private GptOssService gptOssService;

    // 테스트 시작 전에 MockWebServer를 시작
    @BeforeAll
    static void setUp() throws IOException {
        mockWebServer = new MockWebServer();
        mockWebServer.start();
    }

    // GptOssService 인스턴스를 초기화
    @BeforeEach
    void initialize() {
        // WebClient 설정
        String baseUrl = String.format("http://localhost:%s", mockWebServer.getPort());

        gptOssService = new GptOssService(
                WebClient.builder(),
                baseUrl,
                "gpt-property-model"
        );
    }

    // 테스트 종료 후에 MockWebServer를 종료
    @AfterAll
    static void tearDown() throws IOException {
        mockWebServer.shutdown();
    }

    @Test
    void testGenerateChecklist_Success() {
        // MockResponse 설정. 이 응답을 반환하는지 확인하는 로직 작성
        String mockResponseJson = """
            {
                "content": ["항목 1", "항목 2", "항목 3"]
            }
        """;

        // MockWebServer에 응답 큐 삽입
        mockWebServer.enqueue(
                new MockResponse()
                        .setResponseCode(200)
                        .setHeader("Content-Type", "application/json")
                        .setBody(mockResponseJson)
        );

        // request 생성
        GptChecklistGenerateRequest request = GptChecklistGenerateRequest.builder()
                .propertyId(1L)
                .name("테스트 매물")
                .address("경상북도 의성군 봉양면 어쩌구")
                .propertyType(PropertyType.OTHER)
                .floor(3)
                .buildYear(2025)
                .area(BigDecimal.valueOf(84.50))
                .availableDate(LocalDate.now())
                .build();

        // 서비스 호출 & 실제 데이터로 변환
        Mono<ChecklistGenerateResponse> monoResponse = gptOssService.generateChecklist(request);

        ChecklistGenerateResponse actualResponse = monoResponse.block();

        // 결과 검증, MockResponse와 일치하는지 확인
        assertThat(actualResponse).isNotNull();
        assertThat(actualResponse.getContent()).containsExactly("항목 1", "항목 2", "항목 3");
    }
}
