package com.example.P2PLending.repository;

import com.example.P2PLending.entity.LoanRequest;
import com.example.P2PLending.entity.LoanStatus;
import com.example.P2PLending.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface LoanRequestRepository extends JpaRepository<LoanRequest, Long> {

    List<LoanRequest> findByBorrower(User borrower);

    // --- TỆP MỚI CỦA SPRINT 3 ---
    // Dùng cho Admin (tìm PENDING) và Lender (tìm APPROVED)
    List<LoanRequest> findByStatus(LoanStatus status); 
}