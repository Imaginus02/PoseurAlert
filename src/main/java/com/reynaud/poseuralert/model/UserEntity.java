package com.reynaud.poseuralert.model;


import com.reynaud.poseuralert.security.SpringSecurityConfig;
import jakarta.persistence.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;


@Entity
@Table(name = "SP_USER")
public class UserEntity implements UserDetails {

    @Id
    @GeneratedValue
    private Long id;

    @Column(nullable = false, name = "email")
    private String email;


    @Column(nullable = false)
    private String password;

    public UserEntity(String email, String password) {
        this.email = email;
        this.password = password;
    }

    public UserEntity() {

    }

    public Long getId() {
        return id;
    }

    public String getEmail() {
        return email;
    }

    public String getUsername() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // Return the authorities/roles for the user
        // For example, you might have a role called "ROLE_USER"
        // You can use SimpleGrantedAuthority for simplicity
        // You might have more complex logic based on your application's roles and permissions
        return List.of(new SimpleGrantedAuthority(SpringSecurityConfig.ROLE_USER));
    }
}
