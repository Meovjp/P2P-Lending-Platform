package com.example.P2PLending.config;

import com.example.P2PLending.security.jwt.JwtAuthenticationFilter;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;

import java.util.Map;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity // Vẫn bật @PreAuthorize
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthFilter;
    private final AuthenticationProvider authenticationProvider;

    /**
     * Bean 1: Cấu hình cho API (Stateless - JWT)
     * Ưu tiên @Order(1) để xử lý các request /api/** trước
     */
    @Bean
    @Order(1)
    public SecurityFilterChain apiSecurityFilterChain(HttpSecurity http) throws Exception {
        http
            // Chỉ áp dụng cấu hình này cho các URL bắt đầu bằng /api/
            .securityMatcher("/api/**") 
            .csrf(csrf -> csrf.disable()) // Vô hiệu hóa CSRF cho API
         // Thêm khối exceptionHandling VÀO ĐÂY
            .exceptionHandling(exceptions -> exceptions
                // Xử lý khi xác thực THẤT BẠI (ví dụ: token sai, chưa login)
                .authenticationEntryPoint((request, response, authException) -> {
                    response.setStatus(HttpStatus.UNAUTHORIZED.value());
                    response.setContentType("application/json");
                    Map<String, String> error = Map.of("error", "Yêu cầu xác thực (401)");
                    new ObjectMapper().writeValue(response.getWriter(), error);
                })
                // Xử lý khi đã xác thực nhưng KHÔNG CÓ QUYỀN
                .accessDeniedHandler((request, response, accessDeniedException) -> {
                    response.setStatus(HttpStatus.FORBIDDEN.value());
                    response.setContentType("application/json");
                    Map<String, String> error = Map.of("error", "Không có quyền truy cập (403)");
                    new ObjectMapper().writeValue(response.getWriter(), error);
                })
            )
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/api/auth/**") // API Đăng nhập/Đăng ký
                .permitAll()
                .anyRequest() // Tất cả các /api/** khác
                .authenticated() // Đều phải xác thực
            )
            .sessionManagement(session -> session
                // Cấu hình API là STATELESS
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS) 
            )
            .authenticationProvider(authenticationProvider) // Dùng chung provider
            .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class); // Thêm Filter JWT

        return http.build();
    }

    /**
     * Bean 2: Cấu hình cho Web (Stateful - Session)
     * @Order(2) sẽ xử lý tất cả các request CÒN LẠI (/**)
     */
    @Bean
    @Order(2)
    public SecurityFilterChain webSecurityFilterChain(HttpSecurity http) throws Exception {
        http
            // Áp dụng cho tất cả các request còn lại
            // Bật CSRF cho Web (Thymeleaf tự hỗ trợ)
            .authorizeHttpRequests(auth -> auth
                // Cho phép truy cập trang login và các file tĩnh (css, js)
                .requestMatchers("/login", "/webjars/**", "/css/**", "/js/**"
                ).permitAll()
                .anyRequest() // Tất cả các request khác
                .authenticated() // Đều phải xác thực
            )
            // Kích hoạt Form Login (Stateful)
            .formLogin(form -> form
                .loginPage("/login") // Đường dẫn đến trang login custom
                .defaultSuccessUrl("/dashboard", true) // Trang sau khi login thành công
                .failureUrl("/login?error=true") // Trang khi login thất bại
                .permitAll()
            )
            // Kích hoạt Logout
            .logout(logout -> logout
                .logoutUrl("/logout")
                .logoutSuccessUrl("/login?logout=true")
                .permitAll()
            )
            .authenticationProvider(authenticationProvider); // Dùng chung provider

        return http.build();
    }
}