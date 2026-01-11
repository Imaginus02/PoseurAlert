package com.reynaud.poseuralert.security;

import com.reynaud.poseuralert.dao.UserDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import java.util.List;

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
        return encoder;
    }

    @Bean
    public UserDetailsService userDetailsService(UserDao userDao, PasswordEncoder passwordEncoder) {
        return new JpaUserDetailsService(userDao, passwordEncoder);
    }



    @Bean
    @Order(2)
    public SecurityFilterChain basicFilterChain(HttpSecurity http) throws Exception {
        System.out.println("Building http");

        http.authorizeHttpRequests((requests) -> requests
                        .antMatchers("/login").permitAll()
                        .antMatchers("/login.html").permitAll()
                        .antMatchers("/login?error=true").permitAll()
                        .antMatchers(HttpMethod.GET, "/api/**").hasRole(ROLE_ADMIN)
                        .antMatchers("/api/sessions/**").permitAll()
                        .antMatchers("/assets/**").permitAll()
                        .antMatchers("/static/**").permitAll()
                        .antMatchers("/inscription").permitAll()
                        .antMatchers("/rendez-vous/**").authenticated()
                        .anyRequest().authenticated()
                )
                .formLogin(formLogin -> formLogin
                        .loginPage("/login")
                        .defaultSuccessUrl("/", true)
                        .loginProcessingUrl("/login/processing")
                        .permitAll()
                        .passwordParameter("password")
                        .usernameParameter("email")
                        .failureUrl("/login?error=true")
                )
                .logout(withDefaults())
                .exceptionHandling(exceptionHandling -> exceptionHandling
                        .authenticationEntryPoint((request, response, authException) -> response.sendRedirect("/login"))
                )
                .csrf();
        return http.build();
    }

}