package com.reynaud.poseuralert.controller;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import com.reynaud.poseuralert.model.UserEntity;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Controller
@CrossOrigin
public class ViewController {

    @GetMapping("/login")
    public String showLoginPage(@AuthenticationPrincipal UserEntity user) {
        System.out.println("=== LOGIN PAGE REQUESTED ===");
        if (user != null) {
            // Si l'utilisateur est déjà connecté, le rediriger vers ses rendez-vous
            return "redirect:/rendez-vous";
        }
        return "login"; // Returns login.html
    }

    @GetMapping("/inscription")
    public String showRegisterPage(@AuthenticationPrincipal UserEntity user) {
        System.out.println("Showing registration page");
        if (user != null) {
            // Si l'utilisateur est déjà connecté, le rediriger vers ses rendez-vous
            return "redirect:/rendez-vous";
        }
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

    @GetMapping("/logout")
    public String logout(HttpServletRequest request, HttpServletResponse response, @AuthenticationPrincipal UserEntity user) {
        System.out.println("=== LOGOUT PAGE REQUESTED ===");
        if (user != null) {
            new SecurityContextLogoutHandler().logout(request, response, org.springframework.security.core.context.SecurityContextHolder.getContext().getAuthentication());
        }
        return "logout";
    }

    @GetMapping("/profil/public/not-found")
    public String publicProfileNotFound() {
        System.out.println("=== PUBLIC PROFILE NOT FOUND ===");
        return "public-profile-not-found";
    }
}
