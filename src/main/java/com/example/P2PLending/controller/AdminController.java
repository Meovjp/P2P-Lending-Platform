package com.example.P2PLending.controller;

import com.example.P2PLending.dto.AdminAssignRateDto;
import com.example.P2PLending.dto.BorrowerProfileDto;
import com.example.P2PLending.dto.LoanResponseDto;
import com.example.P2PLending.entity.BorrowerProfile;
import com.example.P2PLending.service.AdminService;
import com.example.P2PLending.service.BorrowerProfileService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import com.example.P2PLending.entity.LoanRequest;
import com.example.P2PLending.service.LoanService;
import java.util.List;
@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')") // Bảo vệ toàn bộ controller
public class AdminController {

    private final AdminService adminService;
    private final BorrowerProfileService profileService; // Dùng lại service để xem profile
 // --- TỆP MỚI CỦA SPRINT 3 ---
    private final LoanService loanService;
    /**
     * API để Admin xem hồ sơ của một Borrower (Yêu cầu 3)
     */
    @GetMapping("/profile/{userId}")
    public ResponseEntity<BorrowerProfileDto> getBorrowerProfile(@PathVariable Long userId) {
        // Tạm thời dùng username, nhưng an toàn hơn là dùng ID
        // Giả sử service có hàm getById
        // BorrowerProfileDto profile = profileService.getProfileById(userId);
        // return ResponseEntity.ok(profile);
        // Tạm thời comment out nếu chưa có hàm getProfileById
        return ResponseEntity.ok().build(); // Placeholder
    }

    /**
     * API để Admin gán lãi suất cho Borrower (Yêu cầu 2)
     */
    @PostMapping("/profile/{userId}/assign-rate")
    public ResponseEntity<Void> assignRate(
            @PathVariable Long userId,
            @RequestBody AdminAssignRateDto dto) {
        
        adminService.assignInterestRate(userId, dto.getInterestRate());
        return ResponseEntity.ok().build();
    }
    
// --- TỆP MỚI CỦA SPRINT 3 ---
    
    /**
     * Lấy danh sách các khoản vay đang chờ duyệt (PENDING)
     */
    @GetMapping("/loans/pending")
    // Đổi kiểu trả về: List<LoanResponseDto>
    public ResponseEntity<List<LoanResponseDto>> getPendingLoans() {
        List<LoanResponseDto> pendingLoans = loanService.getPendingLoans(); 
        // Service đã trả về DTO rồi, chỉ việc OK
        return ResponseEntity.ok(pendingLoans);
    }

    /**
     * Admin duyệt khoản vay
     */
    @PostMapping("/loans/{loanId}/approve")
    public ResponseEntity<Void> approveLoan(@PathVariable Long loanId) {
        adminService.approveLoan(loanId);
        return ResponseEntity.ok().build();
    }

    /**
     * Admin từ chối khoản vay
     */
    @PostMapping("/loans/{loanId}/reject")
    public ResponseEntity<Void> rejectLoan(@PathVariable Long loanId) {
        adminService.rejectLoan(loanId);
        return ResponseEntity.ok().build();
    }
}