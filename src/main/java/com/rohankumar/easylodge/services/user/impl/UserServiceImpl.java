package com.rohankumar.easylodge.services.user.impl;

import com.rohankumar.easylodge.dtos.user.UserResponse;
import com.rohankumar.easylodge.dtos.user.profile.UserProfileRequest;
import com.rohankumar.easylodge.entities.user.User;
import com.rohankumar.easylodge.exceptions.ResourceNotFoundException;
import com.rohankumar.easylodge.exceptions.UnAuthorisedException;
import com.rohankumar.easylodge.mappers.user.UserMapper;
import com.rohankumar.easylodge.repositories.user.UserRepository;
import com.rohankumar.easylodge.security.utils.SecurityUtils;
import com.rohankumar.easylodge.services.user.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Override
    public User findUserByEmail(String email) {

        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + email));
    }

    @Override
    public UserResponse getUserProfile() {

       log.info("Getting current user profile");

       User currentUser = SecurityUtils.getCurrentUser();
       if(currentUser == null) {
            throw new UnAuthorisedException("Not authenticated");
       }


    }

    @Override
    public UserResponse updateUserProfile(UserProfileRequest profileRequest) {

        User currentUser = SecurityUtils.getCurrentUser();

        log.info("Updating user profile with id {}", currentUser.getId());


        currentUser.setName(profileRequest.getName());
        currentUser.setGender(profileRequest.getGender());
        currentUser.setDateOfBirth(profileRequest.getDateOfBirth());
        currentUser.setContactNumber(currentUser.getContactNumber());

        if(profileRequest.getProfilePicture() != null)
            currentUser.setProfilePicture(currentUser.getProfilePicture().trim());

        User updatedUser = userRepository.save(currentUser);
        log.info("User updated successfully with id {}", updatedUser.getId());

        return UserMapper.toResponse(updatedUser);
    }
}
