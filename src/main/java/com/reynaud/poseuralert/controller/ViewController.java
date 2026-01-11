package com.reynaud.poseuralert.controller;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import com.reynaud.poseuralert.model.UserEntity;

@Controller
@CrossOrigin
public class ViewController {

    @GetMapping("/login")
    public String showLoginPage() {
        System.out.println("=== SHOWING LOGIN PAGE ===");
        // Check if CSRF is available in the request context
        try {
            org.springframework.security.web.csrf.CsrfToken csrfToken =
                (org.springframework.security.web.csrf.CsrfToken)
                org.springframework.web.context.request.RequestContextHolder
                    .currentRequestAttributes()
                    .getAttribute(org.springframework.security.web.csrf.CsrfToken.class.getName(), 0);
            if (csrfToken != null) {
                System.out.println("CSRF Token available: " + csrfToken.getToken().substring(0, 10) + "...");
            } else {
                System.out.println("CSRF Token NOT available!");
            }
        } catch (Exception e) {
            System.out.println("Error checking CSRF: " + e.getMessage());
        }
        return "login"; // Returns login.html
    }

    @GetMapping("/inscription")
    public String showRegisterPage() {
        System.out.println("Showing registration page");
        return "inscription";
    }

    @GetMapping("/")
    public String home(@AuthenticationPrincipal UserEntity user) {
        if (user != null) {
            return "redirect:/rendez-vous";
        }
        return "index";
    }
}
