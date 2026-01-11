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
        System.out.println("=== LOGIN PAGE REQUESTED ===");
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
            // Si l'utilisateur est connecté et arrive sur "/", le rediriger vers ses rendez-vous
            return "redirect:/rendez-vous";
        }
        return "index";
    }
}
