package com.example.P2PLending.dto;

import com.example.P2PLending.entity.LoanRequest;
import com.example.P2PLending.entity.LoanStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoanResponseDto {
    private Long loanId;
    private BigDecimal amount;
    private Integer termMonths;
    private String purpose;
    private LoanStatus status;
    private Double interestRate;
    private LocalDateTime createdAt;
    
    // Thông tin Lender (chỉ hiện khi đã được đầu tư - FUNDED)
    private String lenderName; 

    // Hàm chuyển đổi từ Entity sang DTO
    public static LoanResponseDto fromEntity(LoanRequest loan) {
        LoanResponseDto dto = new LoanResponseDto();
        dto.setLoanId(loan.getLoanId());
        dto.setAmount(loan.getAmount());
        dto.setTermMonths(loan.getTermMonths());
        dto.setPurpose(loan.getPurpose());
        dto.setStatus(loan.getStatus());
        dto.setInterestRate(loan.getInterestRate());
        dto.setCreatedAt(loan.getCreatedAt());

        // Logic an toàn: Chỉ lấy username nếu lender tồn tại
        if (loan.getLender() != null) {
            dto.setLenderName(loan.getLender().getUsername());
        } else {
            dto.setLenderName("Chưa có nhà đầu tư");
        }
        
        return dto;
    }
}