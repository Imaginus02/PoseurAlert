package com.reynaud.poseuralert.dao;

import com.reynaud.poseuralert.model.ClientPhoneEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ClientPhoneDao extends JpaRepository<ClientPhoneEntity, Long> {

    Optional<ClientPhoneEntity> findByPhoneNumber(String phoneNumber);

    boolean existsByPhoneNumber(String phoneNumber);
}