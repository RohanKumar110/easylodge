package com.rohankumar.easylodge.services.booking;

import com.rohankumar.easylodge.dtos.booking.BookingRequest;
import com.rohankumar.easylodge.dtos.booking.BookingResponse;
import com.rohankumar.easylodge.dtos.booking.BookingStatusResponse;
import com.rohankumar.easylodge.dtos.guest.GuestRequest;
import com.rohankumar.easylodge.dtos.guest.GuestResponse;
import com.rohankumar.easylodge.dtos.payment.PaymentResponse;
import com.rohankumar.easylodge.entities.booking.Booking;
import com.stripe.model.Event;

import java.util.List;
import java.util.UUID;

public interface BookingService {

    BookingResponse initializeBooking(BookingRequest bookingRequest);

    List<GuestResponse> createGuests(UUID id, List<GuestRequest> guests);

    BookingStatusResponse getBookingStatus(UUID id);

    PaymentResponse initiatePayment(UUID id);

    void capturePayment(Event event);

    void cancelBooking(UUID id);

    boolean hasBookingExpired(Booking id);

    void deleteGuest(UUID id, UUID guestId);
}
