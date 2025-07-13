package com.rohankumar.easylodge.dtos.booking;

import com.rohankumar.easylodge.dtos.guest.GuestResponse;
import com.rohankumar.easylodge.dtos.hotel.HotelResponse;
import com.rohankumar.easylodge.dtos.room.RoomResponse;
import com.rohankumar.easylodge.dtos.user.UserResponse;
import com.rohankumar.easylodge.enums.booking.BookingStatus;
import lombok.*;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BookingResponse {

    private UUID id;
    private BookingStatus status;
    private HotelResponse hotel;
    private RoomResponse room;
    private UserResponse user;
    private Integer numberOfRooms;
    private LocalDate checkInDate;
    private LocalDate checkOutDate;
    private List<GuestResponse> guests;
}
