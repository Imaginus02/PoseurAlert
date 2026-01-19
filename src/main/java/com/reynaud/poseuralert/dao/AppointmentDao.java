package com.reynaud.poseuralert.dao;

import com.reynaud.poseuralert.model.AppointmentEntity;
import com.reynaud.poseuralert.model.UserEntity;
import com.reynaud.poseuralert.model.AppointmentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface AppointmentDao extends JpaRepository<AppointmentEntity, Long> {

    List<AppointmentEntity> findByProfessional(UserEntity professional);

    List<AppointmentEntity> findByProfessionalAndAppointmentDateBetween(
            UserEntity professional,
            LocalDateTime startDate,
            LocalDateTime endDate);

    @Query("SELECT a FROM AppointmentEntity a WHERE a.professional = :professional AND a.clientPhone = :phoneNumber ORDER BY a.appointmentDate DESC")
    List<AppointmentEntity> findByProfessionalAndClientPhone(
            @Param("professional") UserEntity professional,
            @Param("phoneNumber") String phoneNumber);

    List<AppointmentEntity> findByProfessionalAndStatus(
            UserEntity professional,
            AppointmentStatus status);

        long countByStatus(AppointmentStatus status);

        long countByAppointmentDateAfter(LocalDateTime after);
}