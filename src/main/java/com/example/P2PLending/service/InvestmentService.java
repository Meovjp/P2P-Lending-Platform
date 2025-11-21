package com.example.P2PLending.service;

import com.example.P2PLending.entity.Investment;
import com.example.P2PLending.entity.LoanRequest;
import com.example.P2PLending.entity.LoanStatus;
import com.example.P2PLending.entity.TransactionType;
import com.example.P2PLending.entity.User;
import com.example.P2PLending.repository.InvestmentRepository;
import com.example.P2PLending.repository.LoanRequestRepository;
import com.example.P2PLending.repository.UserRepository;
import lombok.RequiredArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class InvestmentService {

	private final LoanRequestRepository loanRequestRepository;
	private final InvestmentRepository investmentRepository;
	private final UserRepository userRepository;

	// --- Inject thêm 2 service mới Sprint 4---
	private final RepaymentService repaymentService;
	private final TransactionService transactionService;

	/**
	 * Lender thực hiện đầu tư vào một khoản vay
	 */
	@Transactional
	public void invest(Long loanId, String lenderUsername) {
		// 1. Lấy User (Lender)
		User lender = userRepository.findByUserName(lenderUsername)
				.orElseThrow(() -> new UsernameNotFoundException("Lender not found"));

		// 2. Tìm LoanRequest
		LoanRequest loan = loanRequestRepository.findById(loanId)
				.orElseThrow(() -> new RuntimeException("Loan not found"));

		// 3. Kiểm tra nghiệp vụ - Validate trạng thái
		if (loan.getStatus() != LoanStatus.APPROVED) {
			throw new IllegalStateException("Loan is not approved for investment");
		}
		if (loan.getLender() != null) {
			throw new IllegalStateException("Loan has already been funded");
		}
		
		
		// --- [FIX 3] LOGIC VÍ TIỀN (INVESTMENT) ---
        BigDecimal amount = loan.getAmount();

        // Kiểm tra số dư Lender
        if (lender.getBalance().compareTo(amount) < 0) {
            throw new RuntimeException("Số dư không đủ để thực hiện đầu tư. Vui lòng nạp thêm tiền.");
        }

        // Trừ tiền Lender
        lender.setBalance(lender.getBalance().subtract(amount));
        userRepository.save(lender);

        // Cộng tiền cho Borrower (Giải ngân)
        User borrower = loan.getBorrower();
        borrower.setBalance(borrower.getBalance().add(amount));
        userRepository.save(borrower);

		// 4. Cập nhật LoanRequest: Gán Lender và đổi status
		loan.setLender(lender);
		loan.setStatus(LoanStatus.FUNDED);
		loanRequestRepository.save(loan);

		// 5. Tạo bản ghi Investment (ghi log)
		Investment investment = new Investment();
		investment.setLender(lender);
		investment.setLoan(loan);
		investment.setAmount(loan.getAmount()); // Lender đầu tư 100% số tiền

		investmentRepository.save(investment);

// --- LOGIC MỚI CỦA SPRINT 4 ---

		// 6. Ghi log giao dịch (Tiền từ Lender -> vào Khoản vay)
		transactionService.logTransaction(lender, loan.getBorrower(), // Chuyển cho Borrower
				loan, loan.getAmount(), TransactionType.INVESTMENT);

		// 7. Tự động sinh lịch trả nợ
		repaymentService.generateRepaymentSchedule(loan);
	}

	// THÊM HÀM MỚI CHO WEB CONTROLLER Sprint 3 :

	/**
	 * Lấy các khoản vay đã được duyệt (APPROVED) và sẵn sàng cho đầu tư
	 */
	@Transactional(readOnly = true)
	public List<LoanRequest> getAvailableLoans() {
		return loanRequestRepository.findByStatus(LoanStatus.APPROVED);
	}
	
	// --- HÀM MỚI (Cho Sprint 4 Frontend) ---
    @Transactional(readOnly = true)
    public List<Investment> getInvestmentsByLender(String username) {
        User lender = userRepository.findByUserName(username)
                .orElseThrow(() -> new UsernameNotFoundException("Lender not found"));
        return investmentRepository.findByLender(lender);
    }
}