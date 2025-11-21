package com.example.P2PLending.service;

import com.example.P2PLending.entity.*;
import com.example.P2PLending.repository.LoanRequestRepository;
import com.example.P2PLending.repository.RepaymentScheduleRepository;
import com.example.P2PLending.repository.UserRepository;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.List;
import org.springframework.security.access.AccessDeniedException; // Dùng Exception chuẩns
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import com.example.P2PLending.dto.RepaymentScheduleDto; // Import mới
import java.util.stream.Collectors; // Import mới
 // Logic tính lãi và xử lý thanh toán.
@Service
@RequiredArgsConstructor
public class RepaymentService {

    private final RepaymentScheduleRepository repaymentRepository;
    private final TransactionService transactionService;
    
 // Inject thêm để tìm Loan và kiểm tra chủ sở hữu
    private final LoanRequestRepository loanRequestRepository;
    
    private final UserRepository userRepository;

    /**
     * Tự động tạo lịch trả nợ ngay khi khoản vay được FUNDED.
     * Công thức đơn giản: (Gốc + Lãi toàn bộ) / Số tháng
     */
    @Transactional
    public void generateRepaymentSchedule(LoanRequest loan) {
        BigDecimal principal = loan.getAmount();
        Double rate = loan.getInterestRate(); // Ví dụ: 12.5 (tức 12.5%/năm)
        Integer months = loan.getTermMonths();

        // Tính tổng lãi phải trả: Principal * (Rate/100) * (Months/12)
        BigDecimal totalInterest = principal
                .multiply(BigDecimal.valueOf(rate))
                .multiply(BigDecimal.valueOf(months))
                .divide(BigDecimal.valueOf(1200), 2, RoundingMode.HALF_UP);

        BigDecimal totalAmountToPay = principal.add(totalInterest);
        BigDecimal monthlyPayment = totalAmountToPay.divide(BigDecimal.valueOf(months), 2, RoundingMode.HALF_UP);

        LocalDate dueDate = LocalDate.now().plusMonths(1);

        for (int i = 0; i < months; i++) {
            RepaymentSchedule schedule = new RepaymentSchedule();
            schedule.setLoan(loan);
            schedule.setDueDate(dueDate);
            schedule.setAmountDue(monthlyPayment);
            schedule.setStatus(RepaymentStatus.DUE);
            
            repaymentRepository.save(schedule);

            dueDate = dueDate.plusMonths(1);
        }
    }

   
 // ---  Thực hiện trả nợ (Pay) Borrower thực hiện thanh toán 1 kỳ ---
    @Transactional
    public void payInstallment(Long scheduleId, String username) {
        RepaymentSchedule schedule = repaymentRepository.findById(scheduleId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy lịch trả nợ"));

        // 1. Bảo mật: Kiểm tra người trả có phải là chủ khoản vay không
        if (!schedule.getLoan().getBorrower().getUsername().equals(username)) {
            throw new RuntimeException("Bạn không có quyền thanh toán khoản vay này.");
        }

        // 2. Kiểm tra trạng thái
        if (schedule.getStatus() == RepaymentStatus.PAID) {
            throw new RuntimeException("Kỳ hạn này đã được thanh toán rồi.");
        }

        
        LoanRequest loan = schedule.getLoan();
        List<RepaymentSchedule> allSchedules = repaymentRepository.findByLoan(loan);

        // --- [FIX 2] KIỂM TRA TUẦN TỰ (SEQUENTIAL CHECK) ---
        // Không được phép trả kỳ sau nếu kỳ trước chưa trả
        boolean hasPreviousUnpaid = allSchedules.stream()
                .anyMatch(s -> s.getDueDate().isBefore(schedule.getDueDate())
                        && s.getStatus() != RepaymentStatus.PAID);

        if (hasPreviousUnpaid) {
            throw new RuntimeException("Bạn phải thanh toán các kỳ hạn trước đó theo đúng thứ tự.");
        }

        // --- [FIX 3] LOGIC VÍ TIỀN (REPAYMENT) ---
        User borrower = loan.getBorrower();
        User lender = loan.getLender();
        BigDecimal amountDue = schedule.getAmountDue();

        // (Giả lập: Nếu Borrower không đủ tiền, ta "tạm tha" hoặc bắt nạp tiền. 
        // Ở đây ta làm chặt: Phải đủ tiền trong ví)
        if (borrower.getBalance().compareTo(amountDue) < 0) {
             throw new RuntimeException("Số dư ví không đủ để thanh toán. Vui lòng nạp thêm tiền.");
        }

        // Trừ tiền Borrower
        borrower.setBalance(borrower.getBalance().subtract(amountDue));
        userRepository.save(borrower);

        // Cộng tiền cho Lender (Lãi + Gốc)
        lender.setBalance(lender.getBalance().add(amountDue));
        userRepository.save(lender);
        // ------------------------------------------
        
        
        // 3. Cập nhật trạng thái
        schedule.setStatus(RepaymentStatus.PAID);
        schedule.setPaidDate(LocalDate.now());
        repaymentRepository.save(schedule);

        // 4. Ghi log giao dịch (Tiền từ Borrower -> Lender)
        // Lưu ý: transactionService phải được Inject ở trên
        transactionService.logTransaction(
                schedule.getLoan().getBorrower(),
                schedule.getLoan().getLender(),
                schedule.getLoan(),
                schedule.getAmountDue(),
                TransactionType.REPAYMENT
        );
        
     // --- [FIX 1] TỰ ĐỘNG CẬP NHẬT TRẠNG THÁI KHOẢN VAY (COMPLETED) ---
        // Kiểm tra xem còn kỳ nào chưa trả không?
        boolean isFullyPaid = allSchedules.stream()
                .allMatch(s -> s.getStatus() == RepaymentStatus.PAID); // Vì bản ghi hiện tại đã set PAID rồi

        if (isFullyPaid) {
            loan.setStatus(LoanStatus.COMPLETED);
            loanRequestRepository.save(loan);
        }
    }
    
    /**
     * CẬP NHẬT: Thêm tham số username để kiểm tra quyền và Trả về DTO thay vì Entity
     */
    @Transactional(readOnly = true)
    public List<RepaymentScheduleDto> getScheduleByLoanId(Long loanId, String username) {
        // 1. Tìm khoản vay trước
        LoanRequest loan = loanRequestRepository.findById(loanId)
                .orElseThrow(() -> new RuntimeException("Loan not found"));

        // 2. Logic bảo mật: Kiểm tra xem user có phải là Borrower HOẶC Lender của loan này không
        boolean isBorrower = loan.getBorrower().getUsername().equals(username);
        boolean isLender = loan.getLender() != null && loan.getLender().getUsername().equals(username);

        if (!isBorrower && !isLender) {
            throw new AccessDeniedException("Bạn không có quyền xem lịch trả nợ của khoản vay này.");
        }

     // 3. Lấy dữ liệu và CHUYỂN ĐỔI sang DTO
        List<RepaymentSchedule> schedules = repaymentRepository.findByLoan(loan);
        
        return schedules.stream()
                .map(RepaymentScheduleDto::fromEntity) // Dùng hàm static vừa tạo
                .collect(Collectors.toList());
    }
    
    
    
    // Source hỗ trợ fontend Sprint 4 
    
 // --- HÀM MỚI 1: Lấy lịch trả nợ cho Borrower ---
    /**
     * CẬP NHẬT: Sửa hàm này để trả về DTO
     */
    @Transactional(readOnly = true)
    // Đổi kiểu trả về: List<RepaymentSchedule> -> List<RepaymentScheduleDto>
    public List<RepaymentScheduleDto> getRepaymentScheduleByBorrower(String username) {
        User borrower = userRepository.findByUserName(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        // Lấy danh sách Entity từ DB
        List<RepaymentSchedule> schedules = repaymentRepository.findByLoan_Borrower(borrower);

        // Chuyển đổi sang DTO
        return schedules.stream()
                .map(RepaymentScheduleDto::fromEntity) // Dùng hàm chuyển đổi
                .collect(Collectors.toList());
    }
}