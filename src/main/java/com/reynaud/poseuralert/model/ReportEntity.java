package com.reynaud.poseuralert.model;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "SP_REPORT")
public class ReportEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, name = "reported_phone")
    private String reportedPhone;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "professional_id", nullable = false)
    private UserEntity professional;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "appointment_id")
    private AppointmentEntity appointment;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, name = "reason")
    private ReportReason reason;

    @Column(name = "additional_notes")
    private String additionalNotes;

    @Column(nullable = false, name = "created_at")
    private LocalDateTime createdAt;

    public ReportEntity() {
        this.createdAt = LocalDateTime.now();
    }

    public ReportEntity(String reportedPhone, UserEntity professional, ReportReason reason) {
        this();
        this.reportedPhone = reportedPhone;
        this.professional = professional;
        this.reason = reason;
    }

    public ReportEntity(String reportedPhone, UserEntity professional, AppointmentEntity appointment, ReportReason reason) {
        this(reportedPhone, professional, reason);
        this.appointment = appointment;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getReportedPhone() {
        return reportedPhone;
    }

    public void setReportedPhone(String reportedPhone) {
        this.reportedPhone = reportedPhone;
    }

    public UserEntity getProfessional() {
        return professional;
    }

    public void setProfessional(UserEntity professional) {
        this.professional = professional;
    }

    public AppointmentEntity getAppointment() {
        return appointment;
    }

    public void setAppointment(AppointmentEntity appointment) {
        this.appointment = appointment;
    }

    public ReportReason getReason() {
        return reason;
    }

    public void setReason(ReportReason reason) {
        this.reason = reason;
    }

    public String getAdditionalNotes() {
        return additionalNotes;
    }

    public void setAdditionalNotes(String additionalNotes) {
        this.additionalNotes = additionalNotes;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}