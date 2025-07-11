package com.rohankumar.easylodge.mappers.hotel;

import com.rohankumar.easylodge.dtos.hotel.HotelRequest;
import com.rohankumar.easylodge.dtos.hotel.HotelResponse;
import com.rohankumar.easylodge.entities.common.ContactInfo;
import com.rohankumar.easylodge.entities.hotel.Hotel;

public class HotelMapper {

    public static Hotel toEntity(HotelRequest request) {

        if(request == null) return null;

        ContactInfo contact = ContactInfo.builder()
                .email(request.getEmail())
                .phone(request.getPhone())
                .city(request.getCity())
                .coordinates(request.getCoordinates())
                .address(request.getAddress())
                .build();

        return Hotel.builder()
                .name(request.getName())
                .images(request.getImages())
                .amenities(request.getAmenities())
                .contactInfo(contact)
                .deleted(Boolean.FALSE)
                .build();
    }

    public static HotelResponse toResponse(Hotel hotel) {

        if (hotel == null) return null;

        ContactInfo contact = hotel.getContactInfo();

        return HotelResponse.builder()
                .id(hotel.getId())
                .name(hotel.getName())
                .active(hotel.getActive())
                .images(hotel.getImages())
                .amenities(hotel.getAmenities())
                .email(contact != null ? contact.getEmail() : null)
                .phone(contact != null ? contact.getPhone() : null)
                .city(contact != null ? contact.getCity() : null)
                .coordinates(contact != null ? contact.getCoordinates() : null)
                .address(contact != null ? contact.getAddress() : null)
                .build();
    }
}
