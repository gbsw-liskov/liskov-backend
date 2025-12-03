package com.example.liskovbackend.dto.loan.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class LoanRequest {

    @NotNull
    private Integer age;
    @NotNull
    private boolean isHouseholder;
    @NotNull
    private FamilyType familyType;
    @NotNull
    private Integer annualSalary;
    @NotNull
    private Integer monthlySalary;
    @NotNull
    private IncomeType incomeType;
    @NotNull
    private IncomeCategory incomeCategory;
    @NotNull
    private String rentalArea;
    @NotNull
    private HouseType houseType;
    @NotNull
    private RentalType rentalType;
    @NotNull
    private Integer deposit;
    @NotNull
    private Integer managementFee;
    @NotNull
    private boolean availableLoan;
    @NotNull
    private Integer creditRating;
    @NotNull
    private LoanType loanType;
    @NotNull
    private boolean overdueRecord;
    @NotNull
    private boolean hasLeaseAgreement;
    @NotNull
    private Confirmed confirmed;

}
