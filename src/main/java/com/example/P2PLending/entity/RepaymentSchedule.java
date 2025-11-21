package com.example.P2PLending.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.math.BigDecimal;
import java.time.LocalDate;

//Lưu lịch trả nợ (Ví dụ: Vay 6 tháng sẽ có 6 bản ghi trong bảng này cho khoản vay đó).
@Entity
@Table(name = "repayment_schedules")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RepaymentSchedule {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "schedule_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "loan_id", nullable = false)
    private LoanRequest loan;

    @Column(name = "due_date", nullable = false)
    private LocalDate dueDate; // Ngày phải trả

    @Column(name = "amount_due", nullable = false)
    private BigDecimal amountDue; // Số tiền phải trả (Gốc + Lãi)

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RepaymentStatus status; // DUE, PAID

    @Column(name = "paid_date")
    private LocalDate paidDate; // Ngày thực tế trả
}