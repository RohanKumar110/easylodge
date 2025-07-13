package com.rohankumar.easylodge.controllers.hotel;

import com.rohankumar.easylodge.dtos.booking.BookingRequest;
import com.rohankumar.easylodge.dtos.booking.BookingResponse;
import com.rohankumar.easylodge.dtos.guest.GuestRequest;
import com.rohankumar.easylodge.dtos.guest.GuestResponse;
import com.rohankumar.easylodge.dtos.wrapper.ApiResponse;
import com.rohankumar.easylodge.services.booking.BookingService;
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
@RequestMapping(path = "/bookings")
public class HotelBookingController {

    private final BookingService bookingService;

    @PostMapping("/init")
    public ResponseEntity<ApiResponse<BookingResponse>> initializeBooking(@RequestBody BookingRequest bookingRequest) {

        log.info("Attempting to initialize booking for hotel: {}", bookingRequest.getHotelId());
        BookingResponse bookingResponse = bookingService.initializeBooking(bookingRequest);
        return ResponseEntity.status(HttpStatus.OK).body(
                ApiResponse.success(HttpStatus.OK.value(), "Hotel Booking Initialized Successfully", bookingResponse));
    }

    @PostMapping("/{bookingId}/guests")
    public ResponseEntity<ApiResponse<List<GuestResponse>>> createGuests(
            @PathVariable UUID bookingId, @RequestBody List<GuestRequest> guests) {

        log.info("Attempting to create guests for booking: {}", bookingId);
        List<GuestResponse> guestResponseList = bookingService.createGuests(bookingId, guests);
        return ResponseEntity.status(HttpStatus.OK).body(
                ApiResponse.success(HttpStatus.OK.value(), "Guests Created Successfully", guestResponseList));
    }
}
