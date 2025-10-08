package com.reynaud.poseuralert.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;

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
}
