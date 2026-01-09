package com.reynaud.poseuralert.model;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "SP_AUDIT_LOG")
public class AuditLogEntity {

    @Id
    @GeneratedValue
    private Long id;

    @Column(nullable = false, name = "timestamp")
    private LocalDateTime timestamp;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "professional_id", nullable = false)
    private UserEntity professional;

    @Column(nullable = false, name = "action")
    private String action;

    @Column(name = "resource_type")
    private String resourceType;

    @Column(name = "resource_id")
    private String resourceId;

    @Column(name = "details")
    private String details;

    @Column(name = "ip_address")
    private String ipAddress;

    public AuditLogEntity() {
        this.timestamp = LocalDateTime.now();
    }

    public AuditLogEntity(UserEntity professional, String action, String resourceType, String resourceId) {
        this();
        this.professional = professional;
        this.action = action;
        this.resourceType = resourceType;
        this.resourceId = resourceId;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public UserEntity getProfessional() {
        return professional;
    }

    public void setProfessional(UserEntity professional) {
        this.professional = professional;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getResourceType() {
        return resourceType;
    }

    public void setResourceType(String resourceType) {
        this.resourceType = resourceType;
    }

    public String getResourceId() {
        return resourceId;
    }

    public void setResourceId(String resourceId) {
        this.resourceId = resourceId;
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }
}