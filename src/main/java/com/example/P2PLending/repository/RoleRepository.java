package com.example.P2PLending.repository;

import com.example.P2PLending.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {
    
    // Dùng để gán role mặc định khi đăng ký
    Optional<Role> findByRoleName(String roleName);
}