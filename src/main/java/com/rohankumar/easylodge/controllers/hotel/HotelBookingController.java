package com.rohankumar.easylodge.controllers.hotel;

import com.rohankumar.easylodge.dtos.booking.BookingRequest;
import com.rohankumar.easylodge.dtos.booking.BookingResponse;
import com.rohankumar.easylodge.dtos.booking.BookingStatusResponse;
import com.rohankumar.easylodge.dtos.guest.GuestRequest;
import com.rohankumar.easylodge.dtos.guest.GuestResponse;
import com.rohankumar.easylodge.dtos.payment.PaymentResponse;
import com.rohankumar.easylodge.dtos.wrapper.ApiResponse;
import com.rohankumar.easylodge.services.booking.BookingService;
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
@RequestMapping(path = "/bookings")
public class HotelBookingController {

    private final BookingService bookingService;

    @PostMapping("/init")
    public ResponseEntity<ApiResponse<BookingResponse>> initializeBooking(
            @Valid @RequestBody BookingRequest bookingRequest) {

        log.info("Attempting to initialize booking for hotel: {}", bookingRequest.getHotelId());
        BookingResponse bookingResponse = bookingService.initializeBooking(bookingRequest);
        return ResponseEntity.status(HttpStatus.OK).body(
                ApiResponse.success(HttpStatus.OK.value(), "Hotel Booking Initialized Successfully", bookingResponse));
    }

    @PostMapping("/{bookingId}/guests")
    public ResponseEntity<ApiResponse<List<GuestResponse>>> createGuests(
            @PathVariable UUID bookingId, @Valid @RequestBody List<GuestRequest> guests) {

        log.info("Attempting to create guests for booking: {}", bookingId);
        List<GuestResponse> guestResponseList = bookingService.createGuests(bookingId, guests);
        return ResponseEntity.status(HttpStatus.OK).body(
                ApiResponse.success(HttpStatus.OK.value(), "Guests Created Successfully", guestResponseList));
    }

    @PostMapping("/{bookingId}/payments")
    public ResponseEntity<ApiResponse<PaymentResponse>> initiatePayment(@PathVariable UUID bookingId) {

        log.info("Attempting to initiate payment for booking: {}", bookingId);
        PaymentResponse paymentResponse = bookingService.initiatePayment(bookingId);
        return ResponseEntity.status(HttpStatus.OK).body(
                ApiResponse.success(HttpStatus.OK.value(), "Hotel Booking Payment Initiated Successfully", paymentResponse));
    }

    @PostMapping("/{bookingId}/cancel")
    public ResponseEntity<ApiResponse<Void>> cancelBooking(@PathVariable UUID bookingId) {

        log.info("Attempting to cancel booking: {}", bookingId);
        bookingService.cancelBooking(bookingId);
        return ResponseEntity.status(HttpStatus.OK).body(
                ApiResponse.success(HttpStatus.OK.value(), "Hotel Booking Cancelled Successfully"));
    }

    @GetMapping("/{bookingId}/status")
    public ResponseEntity<ApiResponse<BookingStatusResponse>> getBookingStatus(@PathVariable UUID bookingId) {

        log.info("Attempting to get booking status for booking: {}", bookingId);
        BookingStatusResponse bookingStatusResponse = bookingService.getBookingStatus(bookingId);
        return ResponseEntity.status(HttpStatus.OK).body(
                ApiResponse.success(HttpStatus.OK.value(), "Hotel Status Fetched Successfully",  bookingStatusResponse));
    }

    @DeleteMapping("/{bookingId}/guests/{guestId}")
    public ResponseEntity<ApiResponse<Void>> deleteGuest(
            @PathVariable UUID bookingId, @PathVariable UUID guestId) {

        log.info("Attempting to delete guests for booking: {}", bookingId);
        bookingService.deleteGuest(bookingId, guestId);
        return ResponseEntity.status(HttpStatus.OK).body(
                ApiResponse.success(HttpStatus.NO_CONTENT.value(), "Guest Deleted Successfully"));
    }
}
