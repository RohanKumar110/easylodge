package com.rohankumar.easylodge.mappers.booking;

import com.rohankumar.easylodge.dtos.booking.BookingRequest;
import com.rohankumar.easylodge.dtos.booking.BookingResponse;
import com.rohankumar.easylodge.entities.booking.Booking;
import com.rohankumar.easylodge.mappers.guest.GuestMapper;
import com.rohankumar.easylodge.mappers.hotel.HotelMapper;
import com.rohankumar.easylodge.mappers.room.RoomMapper;
import com.rohankumar.easylodge.mappers.user.UserMapper;

import java.util.stream.Collectors;

public class BookingMapper {

    public static Booking toEntity(BookingRequest request) {

        if(request == null) return null;

        return Booking.builder()
                .numberOfRooms(request.getNumberOfRooms())
                .checkInDate(request.getCheckInDate())
                .checkOutDate(request.getCheckOutDate())
                .build();
    }

    public static BookingResponse toResponse( Booking booking) {

        if(booking == null) return null;

        return BookingResponse.builder()
                .id(booking.getId())
                .status(booking.getStatus())
                .hotel(HotelMapper.toResponse(booking.getHotel()))
                .room(RoomMapper.toResponse(booking.getRoom()))
                .user(UserMapper.toResponse(booking.getUser()))
                .numberOfRooms(booking.getNumberOfRooms())
                .checkInDate(booking.getCheckInDate())
                .checkOutDate(booking.getCheckOutDate())
                .guests(booking.getGuests() != null ?
                        booking.getGuests().stream().map(GuestMapper::toResponse).collect(Collectors.toList()) : null)
                .build();
    }
}
