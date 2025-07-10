package com.rohankumar.easylodge.mappers.room;

import com.rohankumar.easylodge.dtos.room.RoomRequest;
import com.rohankumar.easylodge.dtos.room.RoomResponse;
import com.rohankumar.easylodge.entities.room.Room;

public class RoomMapper {

    public static Room toEntity(RoomRequest request) {

        if(request == null) return null;

        return Room.builder()
                .type(request.getType())
                .basePrice(request.getBasePrice())
                .images(request.getImages())
                .amenities(request.getAmenities())
                .totalRoomsCount(request.getTotalRoomsCount())
                .capacity(request.getCapacity())
                .build();
    }

    public static RoomResponse toResponse(Room room) {

        if(room == null) return null;

        return RoomResponse.builder()
                .id(room.getId())
                .type(room.getType())
                .basePrice(room.getBasePrice())
                .images(room.getImages())
                .amenities(room.getAmenities())
                .totalRoomsCount(room.getTotalRoomsCount())
                .capacity(room.getCapacity())
                .build();
    }
}
