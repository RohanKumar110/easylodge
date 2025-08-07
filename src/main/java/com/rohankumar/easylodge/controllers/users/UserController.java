package com.rohankumar.easylodge.controllers.users;

import com.rohankumar.easylodge.dtos.booking.BookingResponse;
import com.rohankumar.easylodge.dtos.user.UserResponse;
import com.rohankumar.easylodge.dtos.user.profile.UserProfileRequest;
import com.rohankumar.easylodge.dtos.wrapper.ApiResponse;
import com.rohankumar.easylodge.services.booking.BookingService;
import com.rohankumar.easylodge.services.user.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.UUID;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/users")
public class UserController {

    private final UserService userService;
    private final BookingService bookingService;

    @GetMapping("/me/bookings")
    public ResponseEntity<ApiResponse<List<BookingResponse>>> getUserBookings() {

        log.info("Attempting to get current user bookings");
        List<BookingResponse> bookingResponseList = bookingService.getUserBookings();
        return ResponseEntity.status(HttpStatus.OK).body(
                ApiResponse.success(HttpStatus.OK.value(), "User Bookings Fetched Successfully", bookingResponseList));
    }

    public ResponseEntity<ApiResponse<UserResponse>> getUserProfile() {

        log.info("Attempting to get current user profile");
        UserResponse userResponse = userService.getUserProfile();
        return ResponseEntity.status(HttpStatus.OK).body(
                ApiResponse.success(HttpStatus.OK.value(), "User Profile Fetched Successfully", userResponse));
    }

    @PatchMapping("/profile")
    public ResponseEntity<ApiResponse<UserResponse>> updateUserProfile(@Valid UserProfileRequest profileRequest) {

        log.info("Attempting to update current user profile");
        UserResponse userResponse = userService.updateUserProfile(profileRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(
                ApiResponse.success(HttpStatus.CREATED.value(), "User Profile Successfully", userResponse));
    }
}
