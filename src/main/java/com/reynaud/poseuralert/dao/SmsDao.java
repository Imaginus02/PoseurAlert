package com.reynaud.poseuralert.dao;

import com.reynaud.poseuralert.model.SMSEntity;
import com.reynaud.poseuralert.model.SMSCategory;
import com.reynaud.poseuralert.model.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface SmsDao extends JpaRepository<SMSEntity, Long> {

    List<SMSEntity> findByProfessional(UserEntity professional);

    @Query("SELECT COUNT(s) FROM SMSEntity s WHERE s.professional = :professional")
    long countByProfessional(@Param("professional") UserEntity professional);

    @Query("SELECT COUNT(s) FROM SMSEntity s WHERE s.professional = :professional AND s.category = :category")
    long countByProfessionalAndCategory(@Param("professional") UserEntity professional, @Param("category") SMSCategory category);

    @Query("SELECT COUNT(s) FROM SMSEntity s WHERE s.professional = :professional AND s.sentAt >= :startDate AND s.sentAt <= :endDate")
    long countByProfessionalAndDateRange(@Param("professional") UserEntity professional, 
                                          @Param("startDate") LocalDateTime startDate,
                                          @Param("endDate") LocalDateTime endDate);

    @Query("SELECT COUNT(s) FROM SMSEntity s WHERE s.professional = :professional AND s.category = :category AND s.sentAt >= :startDate AND s.sentAt <= :endDate")
    long countByProfessionalCategoryAndDateRange(@Param("professional") UserEntity professional,
                                                  @Param("category") SMSCategory category,
                                                  @Param("startDate") LocalDateTime startDate,
                                                  @Param("endDate") LocalDateTime endDate);
}
