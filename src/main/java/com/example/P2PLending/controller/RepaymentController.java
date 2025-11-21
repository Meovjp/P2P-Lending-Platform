package com.example.P2PLending.controller;

import com.example.P2PLending.dto.RepaymentScheduleDto;
import com.example.P2PLending.entity.RepaymentSchedule;
import com.example.P2PLending.service.RepaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/repayments")
@RequiredArgsConstructor
public class RepaymentController {

    private final RepaymentService repaymentService;

    /**
     * Xem lịch trả nợ của một khoản vay (Dành cho Borrower)
     */
    @GetMapping("/loan/{loanId}")
    @PreAuthorize("hasAnyRole('BORROWER', 'LENDER')")
    public ResponseEntity<List<RepaymentScheduleDto>> getSchedule(@PathVariable Long loanId,Principal principal) {
    	// Truyền username vào service để check quyền
        return ResponseEntity.ok(repaymentService.getScheduleByLoanId(loanId, principal.getName()));
    }

    /**
     * Borrower thanh toán 1 kỳ
     */
    @PostMapping("/{scheduleId}/pay")
    @PreAuthorize("hasRole('BORROWER')")
    public ResponseEntity<String> payInstallment(@PathVariable Long scheduleId, Principal principal) {
        repaymentService.payInstallment(scheduleId, principal.getName());
        return ResponseEntity.ok("Payment successful");
    }
}