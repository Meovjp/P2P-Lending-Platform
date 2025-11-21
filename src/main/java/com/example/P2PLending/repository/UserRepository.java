package com.example.P2PLending.repository;

import com.example.P2PLending.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    
    // Spring Security sẽ dùng hàm này
    Optional<User> findByUserName(String userName);

    // Dùng để kiểm tra khi đăng ký
    Boolean existsByUserName(String userName);
    Boolean existsByEmail(String email);
}