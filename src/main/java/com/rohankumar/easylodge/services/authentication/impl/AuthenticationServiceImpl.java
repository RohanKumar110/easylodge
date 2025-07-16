package com.rohankumar.easylodge.services.authentication.impl;

import com.rohankumar.easylodge.dtos.authentication.AuthenticationResponse;
import com.rohankumar.easylodge.dtos.authentication.AuthenticationRequest;
import com.rohankumar.easylodge.dtos.authentication.SignUpRequest;
import com.rohankumar.easylodge.dtos.user.UserResponse;
import com.rohankumar.easylodge.entities.user.User;
import com.rohankumar.easylodge.enums.role.Role;
import com.rohankumar.easylodge.exceptions.BadRequestException;
import com.rohankumar.easylodge.exceptions.ResourceNotFoundException;
import com.rohankumar.easylodge.mappers.user.UserMapper;
import com.rohankumar.easylodge.repositories.user.UserRepository;
import com.rohankumar.easylodge.security.services.JWTService;
import com.rohankumar.easylodge.services.authentication.AuthenticationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.Optional;
import java.util.Set;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthenticationServiceImpl implements AuthenticationService {

    private final JWTService jwtService;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;

    @Override
    public UserResponse signUp(SignUpRequest signUpRequest) {

        log.info("Signing up user with email: {}", signUpRequest.getEmail());

        Optional<User> optionalUserByEmail = userRepository.findByEmail(signUpRequest.getEmail());
        if(optionalUserByEmail.isPresent()) {
            log.info("User with email: {} already exists", signUpRequest.getEmail());
            throw new BadRequestException("Email already exists");
        }

        User newUser = User.builder()
                .name(signUpRequest.getName())
                .email(signUpRequest.getEmail())
                .password(passwordEncoder.encode(signUpRequest.getPassword()))
                .gender(signUpRequest.getGender())
                .roles(Set.of(Role.GUEST))
                .build();


        User savedUser = userRepository.save(newUser);
        log.info("User signed up successfully with email: {}", signUpRequest.getEmail());

        return UserMapper.toResponse(savedUser);
    }

    @Override
    public AuthenticationResponse login(AuthenticationRequest authenticationRequest) {

        log.info("Authenticating user with email: {}", authenticationRequest.getEmail());

        Optional<User> optionalUserByEmail = userRepository.findByEmail(authenticationRequest.getEmail());
        if(optionalUserByEmail.isEmpty()) {
                log.info("No User found with Email: " + authenticationRequest.getEmail());
                throw new BadCredentialsException("Invalid Email or Password");
        }

        log.info("Verifying User Credentials");

        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(authenticationRequest.getEmail(), authenticationRequest.getPassword());
        authenticationManager.authenticate(authenticationToken);

        log.info("User verified successfully");

        log.info("Generating access and refresh token");

        final String accessToken = jwtService.generateAccessToken(optionalUserByEmail.get());
        final String refreshToken = jwtService.generateRefreshToken(optionalUserByEmail.get());

        AuthenticationResponse authenticationResponse = AuthenticationResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .roles(optionalUserByEmail.get().getRoles())
                .build();

        log.info("User logged in successfully");

        return authenticationResponse;
    }

    @Override
    public AuthenticationResponse renewAccessToken(String refreshToken) {

        log.info("Renewing access token");

        if (StringUtils.isBlank(refreshToken)) {
            log.warn("Refresh token is missing");
            throw new BadRequestException("Invalid refresh token");
        }

        String userEmail = jwtService.extractUsername(refreshToken);
        if (StringUtils.isBlank(userEmail)) {
            log.warn("Token does not contain a valid email");
            throw new BadRequestException("Invalid refresh token");
        }

        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User not found not email: " + userEmail));

        if (!jwtService.isTokenValid(refreshToken, user)) {
            log.warn("Invalid or expired refresh token for user: {}", userEmail);
            throw new BadRequestException("Invalid refresh token");
        }

        log.info("Access token renewed successfully");
        return AuthenticationResponse.builder()
                .accessToken(jwtService.generateAccessToken(user))
                .roles(user.getRoles())
                .build();
    }
}
