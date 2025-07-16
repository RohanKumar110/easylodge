package com.rohankumar.easylodge.security.utils;

import com.rohankumar.easylodge.entities.user.User;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public class SecurityUtils {

    public static User getCurrentUser() {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if(authentication == null || !authentication.isAuthenticated())
            return null;

        Object principal = authentication.getPrincipal();

        return principal instanceof User ? (User) principal : null;
    }
}
