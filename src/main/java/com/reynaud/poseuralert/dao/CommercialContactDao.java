package com.reynaud.poseuralert.dao;

import com.reynaud.poseuralert.model.CommercialContactEntity;
import com.reynaud.poseuralert.model.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommercialContactDao extends JpaRepository<CommercialContactEntity, Long> {

    List<CommercialContactEntity> findByProfessional(UserEntity professional);

    List<CommercialContactEntity> findByProfessionalAndIsActive(UserEntity professional, Boolean isActive);

    @Query("SELECT COUNT(c) FROM CommercialContactEntity c WHERE c.professional = :professional AND c.isActive = true")
    long countActiveByProfessional(@Param("professional") UserEntity professional);

    @Query("SELECT COUNT(c) FROM CommercialContactEntity c WHERE c.professional = :professional")
    long countByProfessional(@Param("professional") UserEntity professional);
}
