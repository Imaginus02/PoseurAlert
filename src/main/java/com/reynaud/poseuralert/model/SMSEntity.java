package com.reynaud.poseuralert.model;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "SP_SMS")
public class SMSEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "professional_id", nullable = false)
    private UserEntity professional;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, name = "category")
    private SMSCategory category;

    @Column(nullable = false, name = "recipient_phone")
    private String recipientPhone;

    @Column(name = "message_content", length = 1000)
    private String messageContent;

    @Column(nullable = false, name = "sent_at")
    private LocalDateTime sentAt;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    public SMSEntity() {
        this.createdAt = LocalDateTime.now();
        this.sentAt = LocalDateTime.now();
    }

    public SMSEntity(UserEntity professional, SMSCategory category, String recipientPhone, String messageContent) {
        this();
        this.professional = professional;
        this.category = category;
        this.recipientPhone = recipientPhone;
        this.messageContent = messageContent;
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

    public SMSCategory getCategory() {
        return category;
    }

    public void setCategory(SMSCategory category) {
        this.category = category;
    }

    public String getRecipientPhone() {
        return recipientPhone;
    }

    public void setRecipientPhone(String recipientPhone) {
        this.recipientPhone = recipientPhone;
    }

    public String getMessageContent() {
        return messageContent;
    }

    public void setMessageContent(String messageContent) {
        this.messageContent = messageContent;
    }

    public LocalDateTime getSentAt() {
        return sentAt;
    }

    public void setSentAt(LocalDateTime sentAt) {
        this.sentAt = sentAt;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
