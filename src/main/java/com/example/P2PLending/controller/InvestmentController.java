package com.example.P2PLending.controller;

import com.example.P2PLending.dto.AvailableLoanDto;
import com.example.P2PLending.service.InvestmentService;
import com.example.P2PLending.service.LoanService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/invest")
@RequiredArgsConstructor
@PreAuthorize("hasRole('LENDER')") // Bảo vệ toàn bộ controller cho Lender
public class InvestmentController {

    private final LoanService loanService;
    private final InvestmentService investmentService;

    /**
     * Lấy danh sách các khoản vay đang kêu gọi vốn (APPROVED)
     * (Trả về DTO đã lọc)
     */
    @GetMapping("/available")
    public ResponseEntity<List<AvailableLoanDto>> getAvailableLoans() {
        List<AvailableLoanDto> availableLoans = loanService.getAvailableLoans().stream()
                .map(AvailableLoanDto::fromEntity)
                .collect(Collectors.toList());
        
        return ResponseEntity.ok(availableLoans);
    }

    /**
     * Lender thực hiện đầu tư
     */
    @PostMapping("/{loanId}")
    public ResponseEntity<Void> investInLoan(@PathVariable Long loanId, Principal principal) {
        investmentService.invest(loanId, principal.getName());
        return ResponseEntity.ok().build();
    }
}