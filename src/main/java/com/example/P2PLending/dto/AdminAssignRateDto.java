package com.example.P2PLending.dto;

import lombok.Data;

// DTO đơn giản để Admin gán lãi suất
@Data
public class AdminAssignRateDto {
    private Double interestRate;
}