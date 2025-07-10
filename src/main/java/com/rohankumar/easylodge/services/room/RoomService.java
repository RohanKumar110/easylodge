package com.rohankumar.easylodge.services.room;

import com.rohankumar.easylodge.dtos.room.RoomRequest;
import com.rohankumar.easylodge.dtos.room.RoomResponse;

import java.util.List;
import java.util.UUID;

public interface RoomService {

    RoomResponse createNewRoom(UUID hotelId, RoomRequest roomRequest);

    RoomResponse getRoomById(UUID id);

    List<RoomResponse> getAllRoomsByHotelId(UUID hotelId);

    boolean deleteRoomById(UUID id);
}
