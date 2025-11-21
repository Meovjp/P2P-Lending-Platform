package com.example.P2PLending.controller.web;

import com.example.P2PLending.entity.Investment;
import com.example.P2PLending.entity.LoanRequest;
import com.example.P2PLending.service.InvestmentService;
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
@PreAuthorize("hasRole('LENDER')") // Bảo vệ toàn bộ controller
public class LenderWebController {

    private final InvestmentService investmentService;

    /**
     * Trang 1: Hiển thị các khoản vay có sẵn (APPROVED)
     */
    @GetMapping("/invest/available")
    public String showAvailableLoansPage(Model model) {
     // Giả sử InvestmentService có hàm này 
        List<LoanRequest> loans = investmentService.getAvailableLoans();
        model.addAttribute("availableLoans", loans);
        model.addAttribute("pageTitle", "Các khoản vay kêu gọi vốn");
        return "lender/available-loans";
    }

    /**
     * Xử lý: Đầu tư vào một khoản vay
     */
    @PostMapping("/invest/{loanId}")
    public String handleInvestment(@PathVariable Long loanId,
                                   Principal principal,
                                   RedirectAttributes redirectAttributes) {
        try {
            investmentService.invest(loanId, principal.getName());
            redirectAttributes.addFlashAttribute("successMessage", "Đầu tư thành công!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Lỗi: " + e.getMessage());
        }
        return "redirect:/invest/available";
    }
    
 // --- SPRINT 4: Xem danh mục đầu tư ---
    @GetMapping("/invest/portfolio")
    public String showPortfolioPage(Model model, Principal principal) {
        // Gọi logic thật từ Backend
        List<Investment> investments = investmentService.getInvestmentsByLender(principal.getName());
        
        model.addAttribute("investments", investments);
        model.addAttribute("pageTitle", "Danh mục đầu tư");
        return "lender/portfolio";
    }
}