package com.example.P2PLending.controller.web;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class WebController {

    /**
     * Hiển thị trang login
     */
    @GetMapping("/login")
    public String loginPage() {
        // Nếu đã login rồi (có Session) thì redirect về dashboard
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated() && !auth.getPrincipal().equals("anonymousUser")) {
            return "redirect:/dashboard";
        }
        return "login"; // Trả về file 'src/main/resources/templates/login.html'
    }

    /**
     * Trang gốc, redirect về dashboard
     */
    @GetMapping("/")
    public String root() {
        return "redirect:/dashboard";
    }
}