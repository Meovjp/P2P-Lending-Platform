package com.example.P2PLending.repository;

import com.example.P2PLending.entity.Transaction;
import com.example.P2PLending.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    // Xem lịch sử giao dịch của một người dùng
    List<Transaction> findByFromUserOrToUser(User fromUser, User toUser);
}