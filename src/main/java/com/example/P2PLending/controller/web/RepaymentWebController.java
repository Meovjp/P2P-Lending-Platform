package com.example.P2PLending.controller.web;

import com.example.P2PLending.dto.RepaymentScheduleDto;
import com.example.P2PLending.dto.TransactionDto;
import com.example.P2PLending.entity.RepaymentSchedule;
import com.example.P2PLending.entity.Transaction;
import com.example.P2PLending.repository.TransactionRepository;
import com.example.P2PLending.service.RepaymentService;
import com.example.P2PLending.service.TransactionService;
import com.example.P2PLending.repository.UserRepository;
import com.example.P2PLending.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;
import java.util.List;

@Controller
@RequiredArgsConstructor
public class RepaymentWebController {

    private final RepaymentService repaymentService;
    private final UserRepository userRepository;
    private final TransactionService transactionService; // Sử dụng Service

    /**
     * Màn hình xem lịch trả nợ của 1 khoản vay
     */
    @GetMapping("/loan/{loanId}/schedule")
    @PreAuthorize("hasAnyRole('BORROWER', 'LENDER', 'ADMIN')")
    public String showRepaymentSchedule(@PathVariable Long loanId, Model model, Principal principal) {
        // Lấy danh sách lịch trả nợ (Backend đã có check quyền sở hữu)
        try {
            List<RepaymentScheduleDto> schedule = repaymentService.getScheduleByLoanId(loanId, principal.getName());
            model.addAttribute("schedules", schedule);
            model.addAttribute("loanId", loanId);
            model.addAttribute("pageTitle", "Lịch trả nợ");
            return "repayment/schedule";
        } catch (Exception e) {
            return "redirect:/dashboard?error=" + e.getMessage();
        }
    }

    /**
     * Xử lý nút "Thanh toán" trên giao diện
     */
    @PostMapping("/repayment/{scheduleId}/pay")
    @PreAuthorize("hasRole('BORROWER')")
    public String handlePayment(@PathVariable Long scheduleId, 
                                Principal principal, 
                                RedirectAttributes redirectAttributes) {
        try {
            repaymentService.payInstallment(scheduleId, principal.getName());
            redirectAttributes.addFlashAttribute("successMessage", "Thanh toán thành công!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Lỗi thanh toán: " + e.getMessage());
        }
        // Redirect lại trang lịch trả nợ để user thấy cập nhật
        // (Cần query DB để lấy loanId từ scheduleId để redirect đúng, nhưng để đơn giản ta quay về danh sách khoản vay)
        return "redirect:/loan/my-requests"; 
    }

    /**
     * Màn hình Lịch sử giao dịch (Dòng tiền)
     */
    @GetMapping("/transactions")
    public String showTransactions(Model model, Principal principal) {
    	// 1. Lấy số dư hiện tại (Vẫn cần User entity cho việc này, hoặc viết hàm riêng)
        User user = userRepository.findByUserName(principal.getName()).orElseThrow();
        
        // 2. Lấy transaction liên quan đến user này - Lấy danh sách giao dịch dưới dạng DTO (AN TOÀN)
        List<TransactionDto> transactions = transactionService.getTransactionHistory(principal.getName());    
        
        model.addAttribute("transactions", transactions);
        model.addAttribute("currentBalance", user.getBalance()); // Hiển thị số dư hiện tại
        model.addAttribute("pageTitle", "Lịch sử giao dịch");
        return "repayment/transactions";
    }
}