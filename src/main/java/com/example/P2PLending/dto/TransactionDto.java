package com.example.P2PLending.dto;

import com.example.P2PLending.entity.Transaction;
import com.example.P2PLending.entity.TransactionType;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class TransactionDto {
    private Long id;
    private BigDecimal amount;
    private TransactionType type;
    private LocalDateTime createdAt;
    
    // Thay vì chứa cả object User (Entity), ta chỉ chứa tên
    private String fromUsername;
    private String toUsername;
    
    // Thay vì chứa cả object LoanRequest, ta chỉ chứa ID
    private Long loanId;

    /**
     * Hàm chuyển đổi an toàn từ Entity -> DTO
     * Được gọi bên trong Transaction của Service
     */
    public static TransactionDto fromEntity(Transaction tx) {
        TransactionDto dto = new TransactionDto();
        dto.setId(tx.getId());
        dto.setAmount(tx.getAmount());
        dto.setType(tx.getType());
        dto.setCreatedAt(tx.getCreatedAt());

        // Xử lý an toàn: Chỉ lấy dữ liệu cần thiết
        if (tx.getFromUser() != null) {
            dto.setFromUsername(tx.getFromUser().getUsername());
        }
        if (tx.getToUser() != null) {
            dto.setToUsername(tx.getToUser().getUsername());
        }
        if (tx.getLoan() != null) {
            dto.setLoanId(tx.getLoan().getLoanId());
        }
        return dto;
    }
}