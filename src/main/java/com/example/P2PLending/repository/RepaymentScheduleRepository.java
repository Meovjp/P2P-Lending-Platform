package com.example.P2PLending.repository;

import com.example.P2PLending.entity.LoanRequest;
import com.example.P2PLending.entity.RepaymentSchedule;
import com.example.P2PLending.entity.User;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface RepaymentScheduleRepository extends JpaRepository<RepaymentSchedule, Long> {
    // Tìm lịch trả nợ của một khoản vay cụ thể
    List<RepaymentSchedule> findByLoan(LoanRequest loan);
    
    // Hỗ trợ WebController Sprint4
 // Tìm tất cả lịch trả nợ của một Borrower (thông qua quan hệ Loan -> Borrower)
    List<RepaymentSchedule> findByLoan_Borrower(User borrower);
}