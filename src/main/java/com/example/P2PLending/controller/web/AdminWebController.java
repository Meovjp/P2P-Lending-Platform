package com.example.P2PLending.controller.web;

import com.example.P2PLending.dto.AdminAssignRateDto;
import com.example.P2PLending.entity.BorrowerProfile;
import com.example.P2PLending.entity.LoanRequest;
import com.example.P2PLending.service.AdminService;
import com.example.P2PLending.service.BorrowerProfileService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')") // Bảo vệ toàn bộ controller
public class AdminWebController {

    private final AdminService adminService;
    private final BorrowerProfileService profileService;

    /**
     * Trang 1: Hiển thị các hồ sơ chờ gán lãi suất (Logic từ Sprint 2)
     */
    @GetMapping("/admin/profiles/review")
    public String showReviewProfilesPage(Model model) {
        // Giả sử AdminService có hàm này
        List<BorrowerProfile> profiles = adminService.getProfilesPendingInterestRate();
        model.addAttribute("profiles", profiles);
        model.addAttribute("pageTitle", "Duyệt Hồ sơ (Gán lãi suất)");
        return "admin/review-profiles";
    }

    /**
     * Xử lý: Gán lãi suất (Logic từ Sprint 2)
     */
    @PostMapping("/admin/profiles/{userId}/assign-rate")
    public String handleAssignRate(@PathVariable Long userId,
                                   @RequestParam("interestRate") Double interestRate,
                                   RedirectAttributes redirectAttributes) {
        try {
            adminService.assignInterestRate(userId, interestRate);
            redirectAttributes.addFlashAttribute("successMessage", "Đã gán lãi suất thành công.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Lỗi: " + e.getMessage());
        }
        return "redirect:/admin/profiles/review";
    }

    /**
     * Trang 2: Hiển thị các khoản vay chờ duyệt (Logic Sprint 3)
     */
    @GetMapping("/admin/loans/pending")
    public String showPendingLoansPage(Model model) {
        List<LoanRequest> loans = adminService.getPendingLoanRequests();
        model.addAttribute("pendingLoans", loans);
        model.addAttribute("pageTitle", "Duyệt Khoản vay");
        return "admin/pending-loans";
    }

    /**
     * Xử lý: Duyệt khoản vay
     */
    @PostMapping("/admin/loans/{loanId}/approve")
    public String handleApproveLoan(@PathVariable Long loanId, RedirectAttributes redirectAttributes) {
        adminService.approveLoan(loanId);
        redirectAttributes.addFlashAttribute("successMessage", "Đã DUYỆT khoản vay.");
        return "redirect:/admin/loans/pending";
    }

    /**
     * Xử lý: Từ chối khoản vay
     */
    @PostMapping("/admin/loans/{loanId}/reject")
    public String handleRejectLoan(@PathVariable Long loanId, RedirectAttributes redirectAttributes) {
        adminService.rejectLoan(loanId); 
        redirectAttributes.addFlashAttribute("successMessage", "Đã TỪ CHỐI khoản vay.");
        return "redirect:/admin/loans/pending";
    }
}