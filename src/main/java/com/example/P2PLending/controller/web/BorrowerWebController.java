package com.example.P2PLending.controller.web;

import com.example.P2PLending.dto.BorrowerProfileDto;
import com.example.P2PLending.dto.LoanRequestDto;
import com.example.P2PLending.dto.LoanResponseDto;
import com.example.P2PLending.entity.LoanRequest;
import com.example.P2PLending.entity.RepaymentSchedule;
import com.example.P2PLending.exception.ProfileNotApprovedException;
import com.example.P2PLending.service.BorrowerProfileService;
import com.example.P2PLending.service.LoanService;
import com.example.P2PLending.service.RepaymentService;

import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import com.example.P2PLending.dto.RepaymentScheduleDto; // Import DTO
import java.security.Principal;
import java.util.List;

@Controller
@RequiredArgsConstructor
@PreAuthorize("hasRole('BORROWER')") // Chỉ Borrower mới vào được đây
public class BorrowerWebController {

    private final BorrowerProfileService profileService;
    private final LoanService loanService;
    private final RepaymentService repaymentService; 

    /**
     * Hiển thị trang Profile (Hồ sơ)
     */
    @GetMapping("/profile")
    public String showProfilePage(Model model, Principal principal) {
        BorrowerProfileDto profileDto;
        try {
            model.addAttribute("profileDto", profileService.getProfileByUsername(principal.getName()));
        } catch (Exception e) {
            model.addAttribute("profileDto", new BorrowerProfileDto());
        }
        
        // THÊM DÒNG NÀY (BẮT BUỘC):
        model.addAttribute("pageTitle", "Hồ sơ của tôi");
        return "borrower/profile";
    }

    /**
     * Xử lý Cập nhật/Tạo Profile
     */
    @PostMapping("/profile")
    public String handleProfileUpdate(@ModelAttribute BorrowerProfileDto profileDto, 
                                      Principal principal, 
                                      RedirectAttributes redirectAttributes) {
        
        profileService.createOrUpdateProfile(principal.getName(), profileDto);
        
        // Gửi thông báo thành công về trang profile
        redirectAttributes.addFlashAttribute("successMessage", 
            "Cập nhật hồ sơ thành công. Lãi suất sẽ bị reset và chờ Admin duyệt lại.");
            
        return "redirect:/profile";    }

    /**
     * Hiển thị trang Yêu cầu vay
     */
    @GetMapping("/loan/request")
    public String showLoanRequestPage(Model model, Principal principal, RedirectAttributes redirectAttributes) {
    	// ... (logic kiểm tra profile và lãi suất) ...
        try {
            BorrowerProfileDto profileDto = profileService.getProfileByUsername(principal.getName());
            if (profileDto.getInterestRate() == null) {
                redirectAttributes.addFlashAttribute("errorMessage", 
                    "Hồ sơ của bạn chưa được Admin phê duyệt lãi suất. Vui lòng chờ.");
                return "redirect:/profile";
            }
            model.addAttribute("loanRequestDto", new LoanRequestDto());
            model.addAttribute("approvedRate", profileDto.getInterestRate());
            
            // THÊM DÒNG NÀY (BẮT BUỘC):
            model.addAttribute("pageTitle", "Tạo yêu cầu vay");
            return "borrower/loan-request";

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Bạn phải tạo hồ sơ trước khi vay.");
            return "redirect:/profile";
        }
    }

    /**
     * Xử lý Tạo yêu cầu vay
     */
    @PostMapping("/loan/request")
    public String handleLoanRequest(@ModelAttribute LoanRequestDto loanDto, 
                                    Principal principal, 
                                    RedirectAttributes redirectAttributes) {
        try {
            loanService.createLoanRequest(loanDto, principal.getName());
            redirectAttributes.addFlashAttribute("successMessage", "Đã gửi yêu cầu vay thành công.");
            return "redirect:/loan/my-requests";
            
        } catch (ProfileNotApprovedException e) {
            // Bắt lỗi nếu profile bị thay đổi (lãi suất về null)
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/profile";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Lỗi: " + e.getMessage());
            return "redirect:/loan/request";
        }
    }

    /**
     * Hiển thị trang "Các khoản vay của tôi"
     */
    @GetMapping("/loan/my-requests")
    public String showMyLoansPage(Model model, Principal principal) {
        // --- CẬP NHẬT Ở ĐÂY ---
        // Trước đây: List<LoanRequest> myLoans = ...
        // Bây giờ: Đổi thành LoanResponseDto
        List<LoanResponseDto> myLoans = loanService.getMyLoanRequests(principal.getName()); 
        
        model.addAttribute("loanRequests", myLoans);
        
        model.addAttribute("pageTitle", "Các khoản vay của tôi");
        return "borrower/my-loans"; 
    }
    
 // --- SPRINT 4: Xem lịch trả nợ ---
    @GetMapping("/loan/repayments")
    public String showRepaymentSchedule(Model model, Principal principal) {
        // Gọi logic thật từ Backend
    	List<RepaymentScheduleDto> schedules = repaymentService.getRepaymentScheduleByBorrower(principal.getName());        
        model.addAttribute("schedules", schedules);
        model.addAttribute("pageTitle", "Lịch trả nợ");
        return "borrower/repayments";
    }

    // --- SPRINT 4: Xử lý thanh toán ---
    @PostMapping("/loan/repay/{scheduleId}")
    public String handleRepayment(@PathVariable Long scheduleId, 
                                  Principal principal,
                                  RedirectAttributes redirectAttributes) {
        try {
            // Gọi logic thật từ Backend
            repaymentService.payInstallment(scheduleId, principal.getName());
            redirectAttributes.addFlashAttribute("successMessage", "Thanh toán thành công!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Lỗi: " + e.getMessage());
        }
        return "redirect:/loan/repayments";
    }
}