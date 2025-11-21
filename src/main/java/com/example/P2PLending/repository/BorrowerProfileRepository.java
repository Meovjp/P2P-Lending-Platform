package com.example.P2PLending.repository;

import com.example.P2PLending.entity.BorrowerProfile;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BorrowerProfileRepository extends JpaRepository<BorrowerProfile, Long> {
	// Sprint 3 web
	List<BorrowerProfile> findByInterestRateIsNull();
}