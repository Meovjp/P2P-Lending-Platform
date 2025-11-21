package com.example.P2PLending.service;

import com.example.P2PLending.entity.BorrowerProfile;
import com.example.P2PLending.entity.LoanRequest;
import com.example.P2PLending.entity.LoanStatus;
import com.example.P2PLending.repository.BorrowerProfileRepository;
import com.example.P2PLending.repository.LoanRequestRepository;

import lombok.RequiredArgsConstructor;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AdminService {

    private final BorrowerProfileRepository profileRepository;
 // --- TỆP MỚI CỦA SPRINT 3 ---
    private final LoanRequestRepository loanRequestRepository;

    /**
     * Logic nghiệp vụ: Admin gán lãi suất thủ công cho Borrower
     */
    @Transactional
    public BorrowerProfile assignInterestRate(Long borrowerUserId, Double interestRate) {
        BorrowerProfile profile = profileRepository.findById(borrowerUserId)
                .orElseThrow(() -> new RuntimeException("Profile not found for user ID: " + borrowerUserId));

        if (interestRate <= 0) {
            throw new IllegalArgumentException("Interest rate must be positive");
        }

        profile.setInterestRate(interestRate);
        return profileRepository.save(profile);
    }

 // --- TỆP MỚI CỦA SPRINT 3 ---

    /**
     * Admin duyệt một khoản vay
     */
    @Transactional
    public void approveLoan(Long loanId) {
        LoanRequest loan = loanRequestRepository.findById(loanId)
                .orElseThrow(() -> new RuntimeException("Loan not found"));

        if (loan.getStatus() != LoanStatus.PENDING) {
            throw new IllegalStateException("Loan is not in PENDING state");
        }

        loan.setStatus(LoanStatus.APPROVED);
        loanRequestRepository.save(loan);
    }

    /**
     * Admin từ chối một khoản vay
     */
    @Transactional
    public void rejectLoan(Long loanId) {
        LoanRequest loan = loanRequestRepository.findById(loanId)
                .orElseThrow(() -> new RuntimeException("Loan not found"));

        if (loan.getStatus() != LoanStatus.PENDING) {
            throw new IllegalStateException("Loan is not in PENDING state");
        }

        loan.setStatus(LoanStatus.REJECTED);
        loanRequestRepository.save(loan);
    }
    
 // THÊM 2 HÀM MỚI CHO WEB CONTROLLER Sprint 3 :

    /**
     * Lấy danh sách hồ sơ đang chờ gán lãi suất
     */
    @Transactional(readOnly = true)
    public List<BorrowerProfile> getProfilesPendingInterestRate() {
        return profileRepository.findByInterestRateIsNull();
    }

    /**
     * Lấy danh sách khoản vay đang chờ Admin duyệt
     */
    @Transactional(readOnly = true)
    public List<LoanRequest> getPendingLoanRequests() {
        return loanRequestRepository.findByStatus(LoanStatus.PENDING);
    }
}