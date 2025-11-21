package com.example.P2PLending.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDate;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BorrowerProfileDto {
    private Long userId;
    private String username;
    
    @NotBlank(message = "Họ tên không được để trống")
    private String fullName;

    @NotNull(message = "Ngày sinh không được để trống")
    private LocalDate dateOfBirth;

    @NotNull(message = "Thu nhập không được để trống")
    @DecimalMin(value = "0.0", inclusive = false, message = "Thu nhập phải lớn hơn 0")
    private BigDecimal monthlyIncome;

    @NotNull(message = "Chỉ số DTI không được để trống")
    private Double dtiRatio;

    // Các trường hệ thống tự tính
    private Integer riskScore;
    private String riskCategory;

    // Trường Admin gán (có thể là null)
    private Double interestRate;
}