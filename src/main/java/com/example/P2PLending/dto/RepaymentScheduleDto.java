package com.example.P2PLending.dto;

import com.example.P2PLending.entity.RepaymentSchedule;
import com.example.P2PLending.entity.RepaymentStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RepaymentScheduleDto {
    private Long id;
    private Long loanId; // Chỉ lấy ID, không lấy cả object LoanRequest
    private LocalDate dueDate;
    private BigDecimal amountDue;
    private RepaymentStatus status;
    private LocalDate paidDate;

    // Hàm chuyển đổi từ Entity sang DTO
    public static RepaymentScheduleDto fromEntity(RepaymentSchedule schedule) {
        RepaymentScheduleDto dto = new RepaymentScheduleDto();
        dto.setId(schedule.getId());
        // Lấy loanId từ quan hệ loan. getId() trên proxy là an toàn, không kích hoạt lazy loading
        dto.setLoanId(schedule.getLoan().getLoanId()); 
        dto.setDueDate(schedule.getDueDate());
        dto.setAmountDue(schedule.getAmountDue());
        dto.setStatus(schedule.getStatus());
        dto.setPaidDate(schedule.getPaidDate());
        
        
        return dto;
    }
}