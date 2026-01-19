package com.reynaud.poseuralert.controller;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/admin")
public class AdminDashboardController {

    @GetMapping
    public String showDashboard(@AuthenticationPrincipal org.springframework.security.core.userdetails.UserDetails userDetails,
                                Model model) {
        if (userDetails == null) {
            return "redirect:/login";
        }

        model.addAttribute("activePage", "admin");
        model.addAttribute("adminEmail", userDetails.getUsername());
        return "admin-dashboard";
    }
}
