package com.reynaud.poseuralert.dao;

import com.reynaud.poseuralert.model.ReportEntity;
import com.reynaud.poseuralert.model.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReportDao extends JpaRepository<ReportEntity, Long> {

    List<ReportEntity> findByProfessional(UserEntity professional);

    @Query("SELECT COUNT(r) FROM ReportEntity r WHERE r.reportedPhone = :phoneNumber")
    long countByReportedPhone(@Param("phoneNumber") String phoneNumber);

    List<ReportEntity> findByReportedPhone(String reportedPhone);

    @Query("SELECT r FROM ReportEntity r WHERE r.professional = :professional AND r.reportedPhone = :phoneNumber")
    List<ReportEntity> findByProfessionalAndReportedPhone(
            @Param("professional") UserEntity professional,
            @Param("phoneNumber") String phoneNumber);
}