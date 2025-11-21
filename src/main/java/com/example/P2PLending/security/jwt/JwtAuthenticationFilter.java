package com.example.P2PLending.security.jwt;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor // Tự động inject các dependency (final)
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UserDetailsService userDetailsService; // Lấy từ ApplicationConfig

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException {

        final String authHeader = request.getHeader("Authorization");
        final String jwt;
        final String username;

        // 1. Kiểm tra header và token
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response); // Bỏ qua và cho đi tiếp
            return;
        }

        jwt = authHeader.substring(7); // Lấy token từ "Bearer "

        // 2. Trích xuất username từ token
        try {
            username = jwtService.extractUsername(jwt);
        } catch (Exception e) {
            // Token lỗi (hết hạn, sai chữ ký...)
            filterChain.doFilter(request, response);
            return;
        }


        // 3. Xác thực nếu user chưa được xác thực
        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            // Load UserDetails (chính là User.java của bạn)
            UserDetails userDetails = this.userDetailsService.loadUserByUsername(username);

            // 4. Kiểm tra token có hợp lệ không
            if (jwtService.isTokenValid(jwt, userDetails)) {
                // Tạo token xác thực
                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                        userDetails,
                        null, // Không cần credentials (password)
                        userDetails.getAuthorities() // Gán roles/authorities
                );
                authToken.setDetails(
                        new WebAuthenticationDetailsSource().buildDetails(request)
                );
                
                // "Báo" cho Spring Security biết user này đã được xác thực
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        }
        
        filterChain.doFilter(request, response); // Cho request đi tiếp
    }
}