package com.reynaud.poseuralert.dao;

import com.reynaud.poseuralert.model.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;


public interface UserDao extends JpaRepository<UserEntity, Long> {

    @Query("select user from UserEntity user where user.email=:email")
    UserEntity findByEmail(@Param("email") String email);
}
