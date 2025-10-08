package com.reynaud.poseuralert.security;

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
    public UserDetailsService userDetailsService() {
        // We create a password encoder
        PasswordEncoder encoder = PasswordEncoderFactories.createDelegatingPasswordEncoder();
        InMemoryUserDetailsManager manager = new InMemoryUserDetailsManager();

        manager.createUser(User.withUsername("user").password(encoder.encode("password")).roles(ROLE_USER).build());
        manager.createUser(User.withUsername("admin").password(encoder.encode("admin")).roles(ROLE_ADMIN, ROLE_USER).build());
        return manager;
    }


    @Bean
    @Order(2)
    public SecurityFilterChain basicFilterChain(HttpSecurity http) throws Exception {
        System.out.println("Building http");
        http.authorizeHttpRequests((requests) -> requests
                        //.requestMatchers(AntPathRequestMatcher.antMatcher("/mainPage.html")).authenticated()
                        .requestMatchers(AntPathRequestMatcher.antMatcher("/login")).permitAll()
                        .requestMatchers(AntPathRequestMatcher.antMatcher("/login.html")).permitAll()
                        .requestMatchers(AntPathRequestMatcher.antMatcher("/login?error=true")).permitAll()
                        .requestMatchers(AntPathRequestMatcher.antMatcher("/register")).permitAll()
                        .requestMatchers(AntPathRequestMatcher.antMatcher(HttpMethod.GET, "/api/**")).hasRole(ROLE_ADMIN)
                        .requestMatchers(AntPathRequestMatcher.antMatcher("/api/sessions/**")).permitAll()
                        .requestMatchers(AntPathRequestMatcher.antMatcher("/assets/**")).permitAll()
                        .requestMatchers(AntPathRequestMatcher.antMatcher("/static/**")).permitAll()
                        .requestMatchers(AntPathRequestMatcher.antMatcher("/inscription")).permitAll()
                        .anyRequest().authenticated()
                )
                .csrf(AbstractHttpConfigurer::disable)
                .headers(headers -> headers.frameOptions(HeadersConfigurer.FrameOptionsConfig::disable))
                .formLogin(formLogin -> formLogin
                        .loginPage("/login")
                        .defaultSuccessUrl("/mainPage.html?prof=true", true)
                        .loginProcessingUrl("/login/processing")
                        .permitAll()
                        .passwordParameter("password")
                        .usernameParameter("email")
                        .failureUrl("/login?error=true")
                )
                .logout(withDefaults())
                .exceptionHandling(exceptionHandling -> exceptionHandling
                        .authenticationEntryPoint((request, response, authException) -> response.sendRedirect("/login"))
                );
        return http.build();
    }

}