package com.rohankumar.easylodge.services.user;

import com.rohankumar.easylodge.dtos.user.UserResponse;
import com.rohankumar.easylodge.dtos.user.profile.UserProfileRequest;
import com.rohankumar.easylodge.entities.user.User;

public interface UserService {

    User findUserByEmail(String email);

    UserResponse getUserProfile();

    UserResponse updateUserProfile(UserProfileRequest profileRequest);
}
