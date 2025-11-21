package com.example.P2PLending.service;

import com.example.P2PLending.dto.TransactionDto;
import com.example.P2PLending.entity.LoanRequest;
import com.example.P2PLending.entity.Transaction;
import com.example.P2PLending.entity.TransactionType;
import com.example.P2PLending.entity.User;
import com.example.P2PLending.repository.TransactionRepository;
import com.example.P2PLending.repository.UserRepository;

import lombok.RequiredArgsConstructor;

import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;
 //Tính toán tiền trả hàng tháng và ghi log.
@Service
@RequiredArgsConstructor
public class TransactionService {
    private final TransactionRepository transactionRepository;
    private final UserRepository userRepository; // Inject thêm để tìm User

    @Transactional
    public void logTransaction(User from, User to, LoanRequest loan, BigDecimal amount, TransactionType type) {
        Transaction transaction = new Transaction();
        transaction.setFromUser(from);
        transaction.setToUser(to);
        transaction.setLoan(loan);
        transaction.setAmount(amount);
        transaction.setType(type);
        transactionRepository.save(transaction);
    }
    
    /**
     * Lấy lịch sử giao dịch của user và chuyển sang DTO
     * Đảm bảo không lộ Entity ra ngoài
     */
    @Transactional(readOnly = true)
    public List<TransactionDto> getTransactionHistory(String username) {
        User user = userRepository.findByUserName(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        List<Transaction> transactions = transactionRepository.findByFromUserOrToUser(user, user);

        // Chuyển đổi Entity -> DTO ngay tại đây
        return transactions.stream()
                .map(TransactionDto::fromEntity)
                .collect(Collectors.toList());
    }
}