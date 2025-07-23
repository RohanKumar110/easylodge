package com.rohankumar.easylodge.services.room;

import com.rohankumar.easylodge.dtos.room.RoomRequest;
import com.rohankumar.easylodge.dtos.room.RoomResponse;
import jakarta.validation.Valid;

import java.util.List;
import java.util.UUID;

public interface RoomService {

    RoomResponse createNewRoom(UUID hotelId, RoomRequest roomRequest);

    RoomResponse getRoomById(UUID id);

    List<RoomResponse> getAllRoomsByHotelId(UUID hotelId);

    RoomResponse updateRoomById(UUID hotelId, UUID roomId, @Valid RoomRequest roomRequest);

    boolean deleteRoomById(UUID id);
}
