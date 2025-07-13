package com.rohankumar.easylodge.services.booking;

import com.rohankumar.easylodge.dtos.booking.BookingRequest;
import com.rohankumar.easylodge.dtos.booking.BookingResponse;
import com.rohankumar.easylodge.dtos.guest.GuestRequest;
import com.rohankumar.easylodge.dtos.guest.GuestResponse;
import com.rohankumar.easylodge.entities.booking.Booking;

import java.util.List;
import java.util.UUID;

public interface BookingService {

    BookingResponse initializeBooking(BookingRequest bookingRequest);

    List<GuestResponse> createGuests(UUID id, List<GuestRequest> guests);

    boolean hasBookingExpired(Booking booking);
}
