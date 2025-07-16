package com.rohankumar.easylodge.services.authentication;

import com.rohankumar.easylodge.dtos.authentication.AuthenticationResponse;
import com.rohankumar.easylodge.dtos.authentication.AuthenticationRequest;
import com.rohankumar.easylodge.dtos.authentication.SignUpRequest;
import com.rohankumar.easylodge.dtos.user.UserResponse;

public interface AuthenticationService {

    UserResponse signUp(SignUpRequest signUpRequest);

    AuthenticationResponse login(AuthenticationRequest authenticationRequest);

    AuthenticationResponse renewAccessToken(String refreshToken);
}
