package com.example.P2PLending.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "investments")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Investment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "investment_id")
    private Long investmentId;

    // Người đầu tư (Lender)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "lender_id", nullable = false)
    private User lender;

    // Khoản vay được đầu tư
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "loan_id", nullable = false)
    private LoanRequest loan;

    @Column(nullable = false)
    private BigDecimal amount; // Số tiền đã đầu tư

    @Column(updatable = false)
    private LocalDateTime investedAt;

    @PrePersist
    protected void onCreate() {
        investedAt = LocalDateTime.now();
    }
}