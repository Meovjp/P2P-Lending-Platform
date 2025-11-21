package com.example.P2PLending.repository;

import com.example.P2PLending.entity.Investment;
import com.example.P2PLending.entity.User;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface InvestmentRepository extends JpaRepository<Investment, Long> {
	// Hỗ trợ WebController Sprint 4
	// Tìm tất cả khoản đầu tư của một Lender
    List<Investment> findByLender(User lender);
}