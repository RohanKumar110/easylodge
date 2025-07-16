package com.rohankumar.easylodge.services.user;

import com.rohankumar.easylodge.entities.user.User;

public interface UserService {

    User findUserByEmail(String email);
}
