package com.example.P2PLending.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoanRequestDto {
	@NotNull(message = "Số tiền không được để trống")
    @DecimalMin(value = "1000000.0", message = "Số tiền vay tối thiểu là 1,000,000 VND")
    private BigDecimal amount;

    @NotNull(message = "Kỳ hạn không được để trống")
    @Min(value = 1, message = "Kỳ hạn tối thiểu 1 tháng")
    @Max(value = 36, message = "Kỳ hạn tối đa 36 tháng")
    private Integer termMonths;

    @NotBlank(message = "Mục đích vay không được để trống")
    private String purpose;
}