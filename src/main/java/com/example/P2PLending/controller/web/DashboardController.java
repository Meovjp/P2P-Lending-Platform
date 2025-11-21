package com.example.P2PLending.controller.web;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.security.Principal;

@Controller
public class DashboardController {

    @GetMapping("/dashboard")
    public String dashboard(Model model, Principal principal) {
        if (principal != null) {
            model.addAttribute("username", principal.getName());
        }
        
        // THÊM DÒNG NÀY (RẤT QUAN TRỌNG):
        model.addAttribute("pageTitle", "Dashboard"); 
        
        return "dashboard"; // Trả về file 'src/main/resources/templates/dashboard.html'
    }
}