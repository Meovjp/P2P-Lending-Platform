package com.example.P2PLending.controller;

import com.example.P2PLending.dto.LoanRequestDto;
import com.example.P2PLending.dto.LoanResponseDto;
import com.example.P2PLending.entity.LoanRequest;
import com.example.P2PLending.service.LoanService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/loans")
@RequiredArgsConstructor
public class LoanRequestController {

    private final LoanService loanService;

    @PostMapping("/request")
    @PreAuthorize("hasRole('BORROWER')")
    // Đổi kiểu trả về: LoanResponseDto
    public ResponseEntity<LoanResponseDto> createLoanRequest(@Valid @RequestBody LoanRequestDto loanDto, Principal principal) {
        // 1. Gọi service tạo Entity
        LoanRequest newLoan = loanService.createLoanRequest(loanDto, principal.getName());
        
        // 2. Chuyển đổi Entity -> DTO ngay tại Controller trước khi trả về
        LoanResponseDto responseDto = LoanResponseDto.fromEntity(newLoan);
        
        return ResponseEntity.status(HttpStatus.CREATED).body(responseDto);
    }

    @GetMapping("/my-requests")
    @PreAuthorize("hasRole('BORROWER')")
    public ResponseEntity<List<LoanResponseDto>> getMyRequests(Principal principal) {
        List<LoanResponseDto> requests = loanService.getMyLoanRequests(principal.getName());
        return ResponseEntity.ok(requests);
    }
}