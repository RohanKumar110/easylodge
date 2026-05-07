package com.rohankumar.easylodge.controllers.authentication;

import com.rohankumar.easylodge.dtos.authentication.AuthenticationRequest;
import com.rohankumar.easylodge.dtos.authentication.AuthenticationResponse;
import com.rohankumar.easylodge.dtos.authentication.SignUpRequest;
import com.rohankumar.easylodge.dtos.user.UserResponse;
import com.rohankumar.easylodge.dtos.wrapper.ApiResponse;
import com.rohankumar.easylodge.services.authentication.AuthenticationService;
import io.jsonwebtoken.lang.Strings;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.time.Duration;
import java.util.Optional;
import java.util.stream.Stream;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/auth")
public class AuthenticationController {

    @Value("${app.security.jwt.refresh.token.expiration}")
    private Long refreshTokenExpirationMillis;

    private final AuthenticationService authenticationService;

    @PostMapping("/signup")
    public ResponseEntity<ApiResponse<UserResponse>> signUp(
            @Valid @RequestBody SignUpRequest signUpRequest) {

        log.info("Attempting to sign up user with email: {}", signUpRequest.getEmail());
        UserResponse userResponse = authenticationService.signUp(signUpRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(
                ApiResponse.success(HttpStatus.CREATED.value(), "User Signed Up Successfully", userResponse));
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<AuthenticationResponse>> login(
            @Valid @RequestBody AuthenticationRequest authenticationRequest) {

        log.info("Attempting to login user with email: {}", authenticationRequest.getEmail());
        AuthenticationResponse authenticationResponse = authenticationService.login(authenticationRequest);

        ResponseCookie refreshCookie = ResponseCookie.from("refreshToken", authenticationResponse.getRefreshToken())
                .httpOnly(true)
                .secure(false)
                .sameSite("Lax")
                .path("/api/v1/auth/refresh")
                .maxAge(Duration.ofMillis(refreshTokenExpirationMillis))
                .build();

        return ResponseEntity.status(HttpStatus.OK)
                .header(HttpHeaders.SET_COOKIE, refreshCookie.toString())
                .body(
                ApiResponse.success(HttpStatus.OK.value(), "User Logged In Successfully", authenticationResponse));
    }

    @PostMapping("/refresh")
    public ResponseEntity<ApiResponse<AuthenticationResponse>> renewAccessToken(
            @NotNull HttpServletRequest request) {

        String refreshToken = Stream.of(Optional.ofNullable(request.getCookies()).orElse(new Cookie[0]))
                .filter(c -> "refreshToken".equals(c.getName()))
                .map(Cookie::getValue)
                .findFirst()
                .orElse(null);

        AuthenticationResponse authenticationResponse = authenticationService.renewAccessToken(refreshToken);
        return ResponseEntity.status(HttpStatus.OK)
                .body(ApiResponse.success(HttpStatus.OK.value(), "Token Renewed Successfully", authenticationResponse));
    }

    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<Void>> logout(HttpServletResponse response) {

        ResponseCookie refreshTokenCookie = ResponseCookie.from("refreshToken", Strings.EMPTY)
                .httpOnly(true)
                .secure(false)
                .sameSite("None")
                .path("/")
                .maxAge(0)
                .build();

        response.addHeader(HttpHeaders.SET_COOKIE, refreshTokenCookie.toString());

        return ResponseEntity.status(HttpStatus.OK)
                .body(ApiResponse.success(HttpStatus.OK.value(), "Logged out successfully", null));
    }
}
