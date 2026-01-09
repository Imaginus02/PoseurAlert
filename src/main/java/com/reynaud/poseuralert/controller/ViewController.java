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
        return "login"; // Returns login.html
    }

    @GetMapping("/inscription")
    public String showRegisterPage() {
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
