package com.example.P2PLending.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.math.BigDecimal;
import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonBackReference;

@Entity
@Table(name = "borrower_profiles")
@Getter
@Setter
@NoArgsConstructor // Bắt buộc cho JPA
@AllArgsConstructor // Thêm để nhất quán
public class BorrowerProfile {

    @Id
    @Column(name = "user_id")
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @MapsId
    @JoinColumn(name = "user_id")
    @JsonBackReference //Tôi là con, khi serialize tôi, đừng quay ngược lại cha (user) nữa
    private User user;

    @Column(name = "full_name")
    private String fullName;

    @Column(name = "dob")
    private LocalDate dateOfBirth;

    @Column(name = "monthly_income")
    private BigDecimal monthlyIncome;

    @Column(name = "dti_ratio")
    private Double dtiRatio;

    // --- Kết quả tính toán của HỆ THỐNG ---
    @Column(name = "risk_score")
    private Integer riskScore;

    @Column(name = "risk_category")
    private String riskCategory; // (vd: 'A', 'B', 'C')

    // --- Kết quả gán của ADMIN ---
    @Column(name = "interest_rate")
    private Double interestRate; // (Sẽ là null cho đến khi Admin gán)
}