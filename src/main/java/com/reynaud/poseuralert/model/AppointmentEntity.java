package com.reynaud.poseuralert.model;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "SP_APPOINTMENT")
public class AppointmentEntity {

    @Id
    @GeneratedValue
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "professional_id", nullable = false)
    private UserEntity professional;

    @Column(nullable = false, name = "client_name")
    private String clientName;

    @Column(nullable = false, name = "client_phone")
    private String clientPhone;

    @Column(nullable = false, name = "appointment_date")
    private LocalDateTime appointmentDate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, name = "status")
    private AppointmentStatus status;

    @Column(name = "notes")
    private String notes;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    public AppointmentEntity() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        this.status = AppointmentStatus.SCHEDULED;
    }

    public AppointmentEntity(UserEntity professional, String clientName, String clientPhone, LocalDateTime appointmentDate) {
        this();
        this.professional = professional;
        this.clientName = clientName;
        this.clientPhone = clientPhone;
        this.appointmentDate = appointmentDate;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public UserEntity getProfessional() {
        return professional;
    }

    public void setProfessional(UserEntity professional) {
        this.professional = professional;
    }

    public String getClientName() {
        return clientName;
    }

    public void setClientName(String clientName) {
        this.clientName = clientName;
    }

    public String getClientPhone() {
        return clientPhone;
    }

    public void setClientPhone(String clientPhone) {
        this.clientPhone = clientPhone;
    }

    public LocalDateTime getAppointmentDate() {
        return appointmentDate;
    }

    public void setAppointmentDate(LocalDateTime appointmentDate) {
        this.appointmentDate = appointmentDate;
        this.updatedAt = LocalDateTime.now();
    }

    public AppointmentStatus getStatus() {
        return status;
    }

    public void setStatus(AppointmentStatus status) {
        this.status = status;
        this.updatedAt = LocalDateTime.now();
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
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