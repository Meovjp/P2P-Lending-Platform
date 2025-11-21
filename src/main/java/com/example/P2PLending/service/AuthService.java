package com.example.P2PLending.service;

import com.example.P2PLending.dto.auth.JwtAuthResponse;
import com.example.P2PLending.dto.auth.LoginRequest;
import com.example.P2PLending.dto.auth.RegisterRequest;
import com.example.P2PLending.entity.Role;
import com.example.P2PLending.entity.User;
import com.example.P2PLending.repository.RoleRepository;
import com.example.P2PLending.repository.UserRepository;
import com.example.P2PLending.security.jwt.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    public JwtAuthResponse register(RegisterRequest request) {
        // 1. Kiểm tra username/email đã tồn tại chưa
        if (userRepository.existsByUserName(request.getUserName())) {
            throw new RuntimeException("Error: Username is already taken!");
        }
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Error: Email is already in use!");
        }

        // 2. Tạo user mới
        User user = new User();
        user.setUserName(request.getUserName());
        user.setEmail(request.getEmail());
        user.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        user.setEnabled(true);

        // 3. Gán Role
        // Thêm "ROLE_" nếu request không có
        String strRole = request.getRole().startsWith("ROLE_") ? request.getRole() : "ROLE_" + request.getRole();
        
        Role userRole = roleRepository.findByRoleName(strRole)
                .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
        
        user.setRoles(Set.of(userRole));

        // 4. Lưu user
        userRepository.save(user);

        // 5. Tạo token và trả về
        var jwtToken = jwtService.generateToken(user);
        return JwtAuthResponse.builder().token(jwtToken).build();
    }

    public JwtAuthResponse login(LoginRequest request) {
        // 1. Xác thực (Spring Security sẽ tự động gọi UserDetailsService và PasswordEncoder)
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getUserName(),
                        request.getPassword()
                )
        );

        // 2. Nếu xác thực thành công, load user và tạo token
        var user = userRepository.findByUserName(request.getUserName())
                .orElseThrow(() -> new RuntimeException("User not found after authentication."));
        
        var jwtToken = jwtService.generateToken(user);
        return JwtAuthResponse.builder().token(jwtToken).tokenType("Bearer").build();
    }
}