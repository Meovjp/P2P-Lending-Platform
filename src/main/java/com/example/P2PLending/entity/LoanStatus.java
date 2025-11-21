package com.example.P2PLending.entity;

public enum LoanStatus {
    PENDING,    // Đang chờ duyệt
    APPROVED,   // Đã được Admin duyệt
    REJECTED,   // Bị Admin từ chối
    FUNDED,     // Đã được Lender đầu tư
    COMPLETED,  // Đã trả hết
    DEFAULTED   // Vỡ nợ
}