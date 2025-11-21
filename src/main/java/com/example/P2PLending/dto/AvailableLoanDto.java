package com.example.P2PLending.dto;

import com.example.P2PLending.entity.LoanRequest;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AvailableLoanDto {

    private Long loanId;
    private BigDecimal amount;
    private Integer termMonths;
    private Double interestRate;
    private String purpose;
    
    // Thông tin không nhạy cảm của Borrower
    private String borrowerRiskCategory; 

    // Helper method để chuyển đổi
    public static AvailableLoanDto fromEntity(LoanRequest loan) {
        return new AvailableLoanDto(
                loan.getLoanId(),
                loan.getAmount(),
                loan.getTermMonths(),
                loan.getInterestRate(),
                loan.getPurpose(),
                loan.getBorrower().getBorrowerProfile().getRiskCategory() // Giả sử User entity có link tới Profile
        );
    }
}