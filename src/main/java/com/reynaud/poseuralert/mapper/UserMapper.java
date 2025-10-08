package com.reynaud.poseuralert.mapper;

import com.reynaud.poseuralert.dto.User;
import com.reynaud.poseuralert.model.UserEntity;


public class UserMapper {
    public static User of(UserEntity user) {
        return new User(
                user.getId(),
                user.getEmail(),
                user.getPassword()
        );
    }
}
