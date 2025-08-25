package com.rohankumar.easylodge.controllers.users;

import com.rohankumar.easylodge.dtos.booking.BookingResponse;
import com.rohankumar.easylodge.dtos.guest.GuestRequest;
import com.rohankumar.easylodge.dtos.guest.GuestResponse;
import com.rohankumar.easylodge.dtos.user.UserResponse;
import com.rohankumar.easylodge.dtos.user.profile.UserProfileRequest;
import com.rohankumar.easylodge.dtos.wrapper.ApiResponse;
import com.rohankumar.easylodge.services.booking.BookingService;
import com.rohankumar.easylodge.services.guest.GuestService;
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
    private final GuestService guestService;
    private final BookingService bookingService;

    @GetMapping("/me/bookings")
    public ResponseEntity<ApiResponse<List<BookingResponse>>> getUserBookings() {

        log.info("Attempting to get current user bookings");
        List<BookingResponse> bookingResponseList = bookingService.getUserBookings();
        return ResponseEntity.status(HttpStatus.OK).body(
                ApiResponse.success(HttpStatus.OK.value(), "User Bookings Fetched Successfully", bookingResponseList));
    }

    @GetMapping("/profile")
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

    @PostMapping("/guests")
    public ResponseEntity<ApiResponse<List<GuestResponse>>> createNewGuests(@RequestBody List<GuestRequest> guestRequestList) {

        log.info("Attempting to create new guests for user");
        List<GuestResponse> guestResponseList = guestService.createNewGuests(guestRequestList);
        return ResponseEntity.status(HttpStatus.CREATED).body(
                ApiResponse.success(HttpStatus.CREATED.value(), "Guests Created Successfully", guestResponseList));
    }

    @GetMapping("/guests")
    public ResponseEntity<ApiResponse<List<GuestResponse>>> getAllGuests() {

        log.info("Attempting to get current user guests");
        List<GuestResponse> guestResponseList = guestService.getAllGuests();
        return ResponseEntity.status(HttpStatus.OK).body(
                ApiResponse.success(HttpStatus.OK.value(), "Guests Created Successfully", guestResponseList));
    }

    @PutMapping("guests/{guestId}")
    public ResponseEntity<ApiResponse<GuestResponse>> updateGuestById(@PathVariable UUID guestId,
                                            @Valid @RequestBody GuestRequest guestRequest) {

        log.info("Attempting to update guest with id: {}", guestId);
        GuestResponse guestResponse = guestService.updateGuestById(guestId, guestRequest);
        return ResponseEntity.status(HttpStatus.OK).body(
                ApiResponse.success(HttpStatus.OK.value(), "Guest Updated Successfully", guestResponse));
    }

    @DeleteMapping("guests/{guestId}")
    public ResponseEntity<ApiResponse<Void>> deleteGuest(@PathVariable UUID guestId) {

        log.info("Attempting to delete guest with id: {}", guestId);
        guestService.deleteGuestById(guestId);
        return ResponseEntity.status(HttpStatus.OK).body(
                ApiResponse.success(HttpStatus.OK.value(), "Guest Deleted Successfully"));
    }
}
