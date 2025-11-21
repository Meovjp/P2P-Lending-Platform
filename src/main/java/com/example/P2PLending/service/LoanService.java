package com.example.P2PLending.service;

import com.example.P2PLending.dto.LoanRequestDto;
import com.example.P2PLending.dto.LoanResponseDto;
import com.example.P2PLending.entity.BorrowerProfile;
import com.example.P2PLending.entity.LoanRequest;
import com.example.P2PLending.entity.LoanStatus;
import com.example.P2PLending.entity.User;
import com.example.P2PLending.exception.ProfileNotApprovedException;
import com.example.P2PLending.repository.BorrowerProfileRepository;
import com.example.P2PLending.repository.LoanRequestRepository;
import com.example.P2PLending.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class LoanService {

    private final LoanRequestRepository loanRequestRepository;
    private final UserRepository userRepository;
    private final BorrowerProfileRepository profileRepository;

    /**
     * CẬP NHẬT: Logic tạo khoản vay (Không đổi logic, chỉ đổi kiểu trả về ở Controller)
     * Hàm này giữ nguyên trả về Entity để nội bộ dùng,
     * Nhưng Controller sẽ convert.
     */
    @Transactional
    public LoanRequest createLoanRequest(LoanRequestDto loanDto, String username) {
        User borrower = userRepository.findByUserName(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        BorrowerProfile profile = profileRepository.findById(borrower.getUserId())
                .orElseThrow(() -> new RuntimeException("Borrower must complete profile first"));

        // YÊU CẦU 2: Kiểm tra Admin đã gán lãi suất chưa
        if (profile.getInterestRate() == null) {
            throw new ProfileNotApprovedException("Hồ sơ của bạn chưa được Admin phê duyệt lãi suất. Vui lòng chờ.");
        }

        // --- Nếu đã được gán, tiếp tục tạo yêu cầu ---
        LoanRequest loanRequest = new LoanRequest();
        loanRequest.setBorrower(borrower);
        loanRequest.setAmount(loanDto.getAmount());
        loanRequest.setTermMonths(loanDto.getTermMonths());
        loanRequest.setPurpose(loanDto.getPurpose());
        loanRequest.setStatus(LoanStatus.PENDING); // Trạng thái chờ Admin duyệt khoản vay

        // Copy lãi suất đã được Admin gán từ Profile sang
        loanRequest.setInterestRate(profile.getInterestRate());

        return loanRequestRepository.save(loanRequest);
    }

    /**
     * ĐÃ CẬP NHẬT: Trả về List LoanResponseDto>
     * Giúp tránh lỗi Hibernate Lazy Loading và ẩn thông tin nhạy cảm.
     */
    @Transactional(readOnly = true)
    public List<LoanResponseDto> getMyLoanRequests(String username) {
        User borrower = userRepository.findByUserName(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        List<LoanRequest> loans = loanRequestRepository.findByBorrower(borrower);

        // Convert Entity -> DTO
        return loans.stream()
                .map(LoanResponseDto::fromEntity)
                .collect(Collectors.toList());
    }
    
 // --- TỆP MỚI CỦA SPRINT 3 ---

    /**
     * Dùng cho Admin: Lấy các khoản vay đang chờ duyệt - Trả về DTO cho Admin duyệt bài
     */
    @Transactional(readOnly = true)
    public List<LoanResponseDto> getPendingLoans() { // Đổi kiểu trả về
        return loanRequestRepository.findByStatus(LoanStatus.PENDING).stream()
                .map(LoanResponseDto::fromEntity) // Convert sang DTO
                .toList();
    }

    /**
     * Dùng cho Lender: Lấy các khoản vay sẵn sàng để đầu tư
     * (Chúng ta sẽ trả về DTO ở bước sau để ẩn thông tin Borrower)
     */
    @Transactional(readOnly = true)
    public List<LoanRequest> getAvailableLoans() {
        return loanRequestRepository.findByStatus(LoanStatus.APPROVED);
    }
}