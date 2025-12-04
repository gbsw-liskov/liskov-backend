package com.example.liskovbackend.service;

import com.example.liskovbackend.dto.loan.request.LoanRequest;
import com.example.liskovbackend.dto.loan.response.LoanResponse;
import com.example.liskovbackend.global.exception.AiNoResponseException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class LoanService {
    private final GptOssService gptOssService;
    @Transactional
    public LoanResponse generateLoanGuide(LoanRequest loanRequest) {
        Mono<LoanResponse> responseMono = gptOssService.generateLoanGuide(loanRequest);

        if(responseMono == null || responseMono.block() == null){
            throw new AiNoResponseException("대출 가이드가 생성되지 않았습니다.");
        }

        return responseMono.block();
    }
}
