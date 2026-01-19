package com.reynaud.poseuralert.security;

import com.reynaud.poseuralert.dao.UserDao;
import com.reynaud.poseuralert.model.UserEntity;
import com.reynaud.poseuralert.util.logging.Loggers;
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
        Loggers.technical().debug("JPA USER DETAILS SERVICE CALLED username={}", username);

        UserEntity userEntity = userDao.findByEmail(username);

        if (userEntity == null) {
            Loggers.access().warn("USER NOT FOUND username={}", username);
            throw new UsernameNotFoundException("User not found: " + username);
        }

        Loggers.business().info("USER LOADED email={} sector={} role={}", userEntity.getEmail(), userEntity.getSector(), userEntity.getRole());
        Loggers.diagnostic().debug("PasswordEncoder={} (hash suppressed)", passwordEncoder.getClass().getSimpleName());

        // Test de vérification du mot de passe avec un mot de passe connu pour le debug
        if ("testpassword".equals("testpassword")) {  // temporaire pour test
            boolean testMatch = passwordEncoder.matches("testpassword", userEntity.getPassword());
            Loggers.diagnostic().debug("Password test match? {}", testMatch);
        }

        String role = userEntity.getRole() != null ? userEntity.getRole() : SpringSecurityConfig.ROLE_USER;

        UserDetails userDetails = User.withUsername(userEntity.getEmail())
            .password(userEntity.getPassword())
            .roles(role)
            .build();

        Loggers.technical().info("UserDetails created for {}", userDetails.getUsername());
        return userDetails;
    }
}