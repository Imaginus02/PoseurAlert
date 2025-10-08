package com.reynaud.poseuralert.controller;

import com.reynaud.poseuralert.dao.UserDao;
import com.reynaud.poseuralert.model.UserEntity;
import com.reynaud.poseuralert.security.SpringSecurityConfig;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@CrossOrigin
@Controller
@RequestMapping("/inscription")
public class RegisterController {

    private final UserDetailsService userDetailsService;

    private final UserDao userDao;

    public RegisterController(UserDao userDao, UserDetailsService userDetailsService) {
        this.userDao = userDao;
        this.userDetailsService = userDetailsService;
    }

    @PostMapping
    public String registerNewUser(@RequestParam String email,
                                       @RequestParam String password,
                                       @RequestParam String passwordConfirm) {

        // Verify if the user entered the same password twice
        if (!password.equals(passwordConfirm))
            return "redirect:/inscription?error=true";

        // Save the new user into the database
        UserEntity user = new UserEntity(email, password);
        UserEntity saved = userDao.save(user);

        PasswordEncoder encoder = PasswordEncoderFactories.createDelegatingPasswordEncoder();
        UserDetails userProfessor = User.withUsername(saved.getUsername())
                .password(encoder.encode(saved.getPassword()))
                .roles(SpringSecurityConfig.ROLE_USER)
                .build();
        if (userDetailsService instanceof InMemoryUserDetailsManager) {
            ((InMemoryUserDetailsManager) userDetailsService).createUser(userProfessor);
        }

        return "login";
    }
}
