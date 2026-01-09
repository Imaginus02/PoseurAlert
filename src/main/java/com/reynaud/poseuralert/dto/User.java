package com.reynaud.poseuralert.dto;

import com.reynaud.poseuralert.model.Sector;

public class User {
    private Long id;
    private String email;
    private String password;
    private String companyName;
    private Sector sector;
    private String address;
    private String phoneNumber;
    private String siret;

    public User() {
    }

    public User(Long id, String email, String password, String companyName, Sector sector, String address, String phoneNumber, String siret) {
        this.id = id;
        this.email = email;
        this.password = password;
        this.companyName = companyName;
        this.sector = sector;
        this.address = address;
        this.phoneNumber = phoneNumber;
        this.siret = siret;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getEmail() {
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
}
