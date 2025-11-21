package com.example.P2PLending.entity;

public enum TransactionType {
    INVESTMENT, // Lender chuyển tiền đầu tư
    REPAYMENT,  // Borrower trả tiền gốc + lãi
    FEE         // Phí hệ thống (nếu có)
}