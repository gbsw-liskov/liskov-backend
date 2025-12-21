package com.example.liskovbackend.dto.loan.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class LoanRequest {

    @NotNull
    private Integer age;
    @NotNull
    @JsonProperty("isHouseholder")
    private boolean isHouseholder;
    @NotNull
    private String familyType;
    @NotNull
    private Integer annualSalary;
    @NotNull
    private Integer monthlySalary;
    @NotNull
    private String incomeType;
    @NotNull
    private String incomeCategory;
    @NotNull
    private String rentalArea;
    @NotNull
    private String houseType;
    @NotNull
    private String rentalType;
    @NotNull
    private Integer deposit;
    @NotNull
    private Integer managementFee;
    @NotNull
    private boolean availableLoan;
    @NotNull
    private Integer creditRating;
    @NotNull
    private String loanType;
    @NotNull
    private boolean overdueRecord;
    @NotNull
    private boolean hasLeaseAgreement;
    @NotNull
    private String confirmed;

}
