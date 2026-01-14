package com.reynaud.poseuralert.security;

import com.reynaud.poseuralert.dao.UserDao;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

import static org.springframework.security.config.Customizer.withDefaults;

@Configuration
@EnableWebSecurity
public class SpringSecurityConfig {// extends WebSecurityConfiguration {

    public static final String ROLE_USER = "USER";
    public static final String ROLE_ADMIN = "ADMIN";

    public SpringSecurityConfig() {
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        PasswordEncoder encoder = PasswordEncoderFactories.createDelegatingPasswordEncoder();
        System.out.println("=== PASSWORD ENCODER CONFIGURED ===");
        System.out.println("PasswordEncoder class: " + encoder.getClass().getSimpleName());
        System.out.println("Application is starting up...");
        return encoder;
    }

    @Bean
    public UserDetailsService userDetailsService(UserDao userDao, PasswordEncoder passwordEncoder) {
        return new JpaUserDetailsService(userDao, passwordEncoder);
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider(UserDetailsService userDetailsService, PasswordEncoder passwordEncoder) {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder);
        System.out.println("=== AUTHENTICATION PROVIDER CONFIGURED ===");
        return authProvider;
    }



    @Bean
    @Order(2)
    public SecurityFilterChain basicFilterChain(HttpSecurity http) throws Exception {
        System.out.println("=== BUILDING SPRING SECURITY FILTER CHAIN ===");

        http.authorizeHttpRequests((requests) -> requests
                        .antMatchers("/").permitAll()
                        .antMatchers("/login").permitAll()
                        .antMatchers("/login.html").permitAll()
                        .antMatchers("/login?error=true").permitAll()
                        .antMatchers("/console/**").permitAll()
                        .antMatchers("/api/**").authenticated()
                        .antMatchers("/api/sessions/**").permitAll()
                        .antMatchers("/assets/**").permitAll()
                        .antMatchers("/static/**").permitAll()
                        .antMatchers("/inscription").permitAll()
                        .antMatchers("/profil/public/**").permitAll()
                        .antMatchers("/rendez-vous/public/**").permitAll()
                        .anyRequest().authenticated()
                )
                .formLogin(formLogin -> formLogin
                        .loginPage("/login")
                        .defaultSuccessUrl("/rendez-vous", true)
                        .loginProcessingUrl("/login/processing")
                        .permitAll()
                        .passwordParameter("password")
                        .usernameParameter("email")
                        .failureUrl("/login?error=true")
                        .successHandler((request, response, authentication) -> {
                            System.out.println("=== LOGIN SUCCESS ===");
                            System.out.println("User: " + authentication.getName());
                            response.sendRedirect("/rendez-vous");
                        })
                        .failureHandler((request, response, exception) -> {
                            System.out.println("=== LOGIN FAILURE ===");
                            System.out.println("Exception: " + exception.getMessage());
                            System.out.println("Username: " + request.getParameter("email"));
                            response.sendRedirect("/login?error=true");
                        })
                )
                .logout(withDefaults())
                .exceptionHandling(exceptionHandling -> exceptionHandling
                        .authenticationEntryPoint((request, response, authException) -> response.sendRedirect("/login"))
                )
                .headers(headers -> headers
                        .frameOptions().disable()
                )
                .csrf(csrf -> csrf
                        .ignoringAntMatchers("/console/**", "/login/processing")
                );
        return http.build();
    }

}