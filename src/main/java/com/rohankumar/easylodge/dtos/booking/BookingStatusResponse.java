package com.rohankumar.easylodge.dtos.booking;

import com.rohankumar.easylodge.enums.booking.BookingStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Service;

@Getter
@Service
@NoArgsConstructor
@AllArgsConstructor
public class BookingStatusResponse {

    private BookingStatus status;
}
