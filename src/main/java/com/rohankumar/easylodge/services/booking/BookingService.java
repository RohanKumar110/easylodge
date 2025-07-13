package com.rohankumar.easylodge.services.booking;

import com.rohankumar.easylodge.dtos.booking.BookingRequest;
import com.rohankumar.easylodge.dtos.booking.BookingResponse;

public interface BookingService {

    BookingResponse initializeBooking(BookingRequest bookingRequest);
}
