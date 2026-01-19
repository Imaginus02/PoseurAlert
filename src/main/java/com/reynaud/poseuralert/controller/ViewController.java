package com.reynaud.poseuralert.controller;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import com.reynaud.poseuralert.model.UserEntity;
import com.reynaud.poseuralert.util.logging.Loggers;

@Controller
@CrossOrigin
public class ViewController {

    @GetMapping("/login")
    public String showLoginPage(@AuthenticationPrincipal UserEntity user) {
        Loggers.access().info("LOGIN PAGE REQUESTED");
        if (user != null) {
            // Si l'utilisateur est déjà connecté, le rediriger vers ses rendez-vous
            return "redirect:/rendez-vous";
        }
        return "login"; // Returns login.html
    }

    @GetMapping("/inscription")
    public String showRegisterPage(@AuthenticationPrincipal UserEntity user) {
        Loggers.access().info("REGISTER PAGE REQUESTED");
        if (user != null) {
            // Si l'utilisateur est déjà connecté, le rediriger vers ses rendez-vous
            return "redirect:/rendez-vous";
        }
        return "inscription";
    }

    @GetMapping("/")
    public String home(@AuthenticationPrincipal UserEntity user) {
        Loggers.access().info("HOME PAGE REQUESTED");
        if (user != null) {
            // Si l'utilisateur est connecté et arrive sur "/", le rediriger vers ses rendez-vous
            return "redirect:/rendez-vous";
        }
        return "index";
    }

    @GetMapping("/logout")
    public String logout() {
        Loggers.access().info("LOGOUT PAGE REQUESTED");
        // La déconnexion est gérée par Spring Security via POST /logout.
        // Cette route sert uniquement à afficher la page de confirmation.
        return "logout";
    }

    @GetMapping("/profil/public/not-found")
    public String publicProfileNotFound() {
        Loggers.access().info("PUBLIC PROFILE NOT FOUND PAGE");
        return "public-profile-not-found";
    }
}
