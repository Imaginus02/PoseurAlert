package com.reynaud.poseuralert.dao;

import com.reynaud.poseuralert.model.AuditLogEntity;
import com.reynaud.poseuralert.model.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AuditLogDao extends JpaRepository<AuditLogEntity, Long> {

    List<AuditLogEntity> findByProfessional(UserEntity professional);

    List<AuditLogEntity> findByProfessionalAndResourceType(UserEntity professional, String resourceType);
}