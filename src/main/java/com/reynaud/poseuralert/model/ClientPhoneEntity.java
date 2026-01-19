package com.reynaud.poseuralert.model;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "SP_CLIENT_PHONE",
       uniqueConstraints = @UniqueConstraint(columnNames = "phone_number"))
public class ClientPhoneEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, name = "phone_number", unique = true)
    private String phoneNumber;

    @Column(nullable = false, name = "report_count")
    private Integer reportCount = 0;

    @Column(name = "last_report_date")
    private LocalDateTime lastReportDate;

    @Column(nullable = false, name = "is_flagged")
    private Boolean isFlagged = false;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    public ClientPhoneEntity() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    public ClientPhoneEntity(String phoneNumber) {
        this();
        this.phoneNumber = phoneNumber;
    }

    // Méthode pour incrémenter le compteur de signalements
    public void incrementReportCount() {
        this.reportCount++;
        this.lastReportDate = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        // Un numéro est considéré comme "flagged" après 2 signalements ou plus
        this.isFlagged = this.reportCount >= 2;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
        this.updatedAt = LocalDateTime.now();
    }

    public Integer getReportCount() {
        return reportCount;
    }

    public void setReportCount(Integer reportCount) {
        this.reportCount = reportCount;
        this.updatedAt = LocalDateTime.now();
        this.isFlagged = this.reportCount >= 2;
    }

    public LocalDateTime getLastReportDate() {
        return lastReportDate;
    }

    public void setLastReportDate(LocalDateTime lastReportDate) {
        this.lastReportDate = lastReportDate;
        this.updatedAt = LocalDateTime.now();
    }

    public Boolean getIsFlagged() {
        return isFlagged;
    }

    public void setIsFlagged(Boolean isFlagged) {
        this.isFlagged = isFlagged;
        this.updatedAt = LocalDateTime.now();
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}