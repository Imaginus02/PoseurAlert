package com.reynaud.poseuralert.model;

import com.reynaud.poseuralert.security.SpringSecurityConfig;
import javax.persistence.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Arrays;
import java.util.Collection;


@Entity
@Table(name = "SP_USER")
public class UserEntity implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, name = "email")
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false, name = "company_name")
    private String companyName;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, name = "sector")
    private Sector sector;

    @Column(name = "address")
    private String address;

    @Column(name = "phone_number")
    private String phoneNumber;

    @Column(name = "siret")
    private String siret;

    @Column(name = "description", length = 1000)
    private String description;

    @Column(name = "business_hours")
    private String businessHours;

    @Column(name = "is_public_profile")
    private Boolean isPublicProfile = false;

    public UserEntity(String email, String password, String companyName, Sector sector) {
        this.email = email;
        this.password = password;
        this.companyName = companyName;
        this.sector = sector;
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

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public Sector getSector() {
        return sector;
    }

    public void setSector(Sector sector) {
        this.sector = sector;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getSiret() {
        return siret;
    }

    public void setSiret(String siret) {
        this.siret = siret;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getBusinessHours() {
        return businessHours;
    }

    public void setBusinessHours(String businessHours) {
        this.businessHours = businessHours;
    }

    public Boolean getIsPublicProfile() {
        return isPublicProfile;
    }

    public void setIsPublicProfile(Boolean isPublicProfile) {
        this.isPublicProfile = isPublicProfile;
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
        return Arrays.asList(new SimpleGrantedAuthority(SpringSecurityConfig.ROLE_USER));
    }
}
