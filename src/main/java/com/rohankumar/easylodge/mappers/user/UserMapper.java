package com.rohankumar.easylodge.mappers.user;

import com.rohankumar.easylodge.dtos.user.UserResponse;
import com.rohankumar.easylodge.entities.user.User;

public class UserMapper {

    public static UserResponse toResponse(User user) {

        if(user == null) return null;

        return UserResponse.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .gender(user.getGender())
                .roles(user.getRoles())
                .build();
    }
}
