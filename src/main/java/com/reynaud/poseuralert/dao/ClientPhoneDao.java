package com.reynaud.poseuralert.dao;

import com.reynaud.poseuralert.model.ClientPhoneEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.List;

@Repository
public interface ClientPhoneDao extends JpaRepository<ClientPhoneEntity, Long> {

    Optional<ClientPhoneEntity> findByPhoneNumber(String phoneNumber);

    boolean existsByPhoneNumber(String phoneNumber);

    long countByIsFlaggedTrue();

    @Query("SELECT c FROM ClientPhoneEntity c WHERE c.isFlagged = true ORDER BY c.reportCount DESC, c.lastReportDate DESC")
    List<ClientPhoneEntity> findFlaggedNumbersOrdered();
}