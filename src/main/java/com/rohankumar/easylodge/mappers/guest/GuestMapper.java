package com.rohankumar.easylodge.mappers.guest;

import com.rohankumar.easylodge.dtos.guest.GuestRequest;
import com.rohankumar.easylodge.dtos.guest.GuestResponse;
import com.rohankumar.easylodge.entities.guest.Guest;

public class GuestMapper {

    public static Guest toEntity(GuestRequest request) {

        if(request == null) return null;

        return Guest.builder()
                .name(request.getName())
                .gender(request.getGender())
                .age(request.getAge())
                .build();
    }

    public static GuestResponse toResponse(Guest guest) {

        if(guest == null) return null;

        return GuestResponse.builder()
                .id(guest.getId())
                .name(guest.getName())
                .gender(guest.getGender())
                .age(guest.getAge())
                .build();
    }
}
