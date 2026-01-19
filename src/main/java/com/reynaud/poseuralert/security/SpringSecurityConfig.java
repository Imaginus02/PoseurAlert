package com.reynaud.poseuralert.security;

import com.reynaud.poseuralert.dao.UserDao;
import com.reynaud.poseuralert.util.logging.Loggers;
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
        Loggers.technical().info("PASSWORD ENCODER CONFIGURED: {}", encoder.getClass().getSimpleName());
        Loggers.technical().info("Application is starting up...");
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
        Loggers.technical().info("AUTHENTICATION PROVIDER CONFIGURED");
        return authProvider;
    }



    @Bean
    @Order(2)
    public SecurityFilterChain basicFilterChain(HttpSecurity http) throws Exception {
        Loggers.technical().info("BUILDING SPRING SECURITY FILTER CHAIN");

        http.authorizeHttpRequests((requests) -> requests
                .antMatchers("/console/**").permitAll()
                .antMatchers("/api/sessions/**").permitAll()
                .antMatchers("/api/admin/**").hasRole(ROLE_ADMIN)
                .antMatchers("/admin/**").hasRole(ROLE_ADMIN)
                .antMatchers("/api/**").authenticated()
                .antMatchers("/").permitAll()
                .antMatchers("/login").permitAll()
                .antMatchers("/login.html").permitAll()
                .antMatchers("/login?error=true").permitAll()
                .antMatchers("/logout").permitAll()
                .antMatchers("/assets/**").permitAll()
                .antMatchers("/static/**").permitAll()
                .antMatchers("/inscription").permitAll()
                .antMatchers("/profil/public/**").permitAll()
                .antMatchers("/rendez-vous/public/**").permitAll()
                .antMatchers( "/favicon.ico").permitAll()
                .anyRequest().authenticated()
                )
                .formLogin(formLogin -> formLogin
                        .loginPage("/login")
                        .loginProcessingUrl("/login/processing")
                        .permitAll()
                        .passwordParameter("password")
                        .usernameParameter("email")
                        .failureUrl("/login?error=true")
                        .successHandler((request, response, authentication) -> {
                            Loggers.access().info("LOGIN SUCCESS user={} ip={}", authentication.getName(), request.getRemoteAddr());
                    boolean isAdmin = authentication.getAuthorities().stream()
                        .anyMatch(auth -> auth.getAuthority().equals("ROLE_" + ROLE_ADMIN));
                    response.sendRedirect(isAdmin ? "/admin" : "/rendez-vous");
                        })
                        .failureHandler((request, response, exception) -> {
                            Loggers.access().warn("LOGIN FAILURE user={} reason={} ip={}", request.getParameter("email"), exception.getMessage(), request.getRemoteAddr());
                            response.sendRedirect("/login?error=true");
                        })
                )
                .logout(logout -> logout
                    .logoutUrl("/logout")
                    .logoutSuccessUrl("/logout")
                    .invalidateHttpSession(true)
                    .clearAuthentication(true)
                    .deleteCookies("JSESSIONID")
                )
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