package com.rohankumar.easylodge.security.handlers;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;

@Slf4j
@Component
public class LogoutHandler implements org.springframework.security.web.authentication.logout.LogoutHandler {

    @Override
    public void logout(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {

        String header = request.getHeader(AUTHORIZATION);
        if(!StringUtils.isBlank(header) && header.startsWith("Bearer ")) {

            log.info("Clearing security context");
            SecurityContextHolder.clearContext();
            log.info("User logged out successfully");
        }
    }
}
