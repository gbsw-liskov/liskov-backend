package com.example.liskovbackend.dto.loan.response;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class LoanResponse {
    private Integer loanAmount;
    private double interestRate;
    private Integer ownCapital;
    private Integer monthlyInterest;
    private Integer managementFee;
    private Integer totalMonthlyCost;
    private List<Item> loans;
    private List<Item> procedures;
    private List<Item> channels;
    private List<Item> advance;
}
