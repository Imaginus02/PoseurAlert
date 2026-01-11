package com.reynaud.poseuralert.security;

import com.reynaud.poseuralert.dao.UserDao;
import com.reynaud.poseuralert.model.UserEntity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class JpaUserDetailsService implements UserDetailsService {

    private final UserDao userDao;
    private final PasswordEncoder passwordEncoder;

    public JpaUserDetailsService(UserDao userDao, PasswordEncoder passwordEncoder) {
        this.userDao = userDao;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        System.out.println("=== JPA USER DETAILS SERVICE CALLED ===");
        System.out.println("Looking for user: " + username);
        System.out.println("Timestamp: " + System.currentTimeMillis());

        UserEntity userEntity = userDao.findByEmail(username);

        if (userEntity == null) {
            System.out.println("ERROR: User not found in database: " + username);
            throw new UsernameNotFoundException("User not found: " + username);
        }

        System.out.println("User found: " + userEntity.getEmail());
        System.out.println("Password hash starts with: " + userEntity.getPassword().substring(0, 10) + "...");
        System.out.println("User sector: " + userEntity.getSector());
        System.out.println("PasswordEncoder class: " + passwordEncoder.getClass().getSimpleName());

        // Test de vérification du mot de passe avec un mot de passe connu pour le debug
        if ("testpassword".equals("testpassword")) {  // temporaire pour test
            boolean testMatch = passwordEncoder.matches("testpassword", userEntity.getPassword());
            System.out.println("TEST: Password 'testpassword' matches hash: " + testMatch);
        }

        UserDetails userDetails = User.withUsername(userEntity.getEmail())
                .password(userEntity.getPassword())
                .roles(SpringSecurityConfig.ROLE_USER)
                .build();

        System.out.println("UserDetails created successfully for: " + userDetails.getUsername());
        return userDetails;
    }
}