package com.example.P2PLending.config;

import com.example.P2PLending.entity.Role;
import com.example.P2PLending.repository.RoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final RoleRepository roleRepository;

    @Override
    public void run(String... args) throws Exception {
        // Tạo các role cơ bản nếu chúng chưa tồn tại
        if (roleRepository.findByRoleName("ROLE_BORROWER").isEmpty()) {
            roleRepository.save(new Role("ROLE_BORROWER"));
        }
        if (roleRepository.findByRoleName("ROLE_LENDER").isEmpty()) {
            roleRepository.save(new Role("ROLE_LENDER"));
        }
        if (roleRepository.findByRoleName("ROLE_ADMIN").isEmpty()) {
            roleRepository.save(new Role("ROLE_ADMIN"));
        }
    }
}