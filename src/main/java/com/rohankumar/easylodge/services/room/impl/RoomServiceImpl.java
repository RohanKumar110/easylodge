package com.rohankumar.easylodge.services.room.impl;

import com.rohankumar.easylodge.dtos.room.RoomRequest;
import com.rohankumar.easylodge.dtos.room.RoomResponse;
import com.rohankumar.easylodge.entities.hotel.Hotel;
import com.rohankumar.easylodge.entities.room.Room;
import com.rohankumar.easylodge.exceptions.ResourceNotFoundException;
import com.rohankumar.easylodge.mappers.room.RoomMapper;
import com.rohankumar.easylodge.repositories.hotel.HotelRepository;
import com.rohankumar.easylodge.repositories.room.RoomRepository;
import com.rohankumar.easylodge.services.room.RoomService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class RoomServiceImpl implements RoomService {

    private final RoomRepository roomRepository;
    private final HotelRepository hotelRepository;

    @Override
    public RoomResponse createNewRoom(UUID hotelId, RoomRequest roomRequest) {

        log.info("Creating the room with type: {} for Hotel with Id: {}", roomRequest.getType(), hotelId);

        log.info("Getting the hotel for room");
        Hotel fetchedHotel = hotelRepository.findById(hotelId).
                orElseThrow(() -> new ResourceNotFoundException("Hotel not found with id: " + hotelId));

        Room room = RoomMapper.toEntity(roomRequest);
        room.setHotel(fetchedHotel);

        Room savedRoom = roomRepository.save(room);
        log.info("Room created successfully with id: {}", savedRoom.getId());

        // Todo: If hotel is active then, create inventory as soon as room is created

        return RoomMapper.toResponse(savedRoom);
    }

    @Override
    public RoomResponse getRoomById(UUID id) {

        log.info("Getting the room with id: {}", id);

        Room fetchedRoom = roomRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Room not found with id: " + id));

        log.info("Room fetched successfully with id: {}", id);

        return RoomMapper.toResponse(fetchedRoom);
    }

    @Override
    public List<RoomResponse> getAllRoomsByHotelId(UUID hotelId) {

        log.info("Getting all the rooms for hotel with id: {}", hotelId);

        log.info("Getting the hotel for rooms");
        Hotel fetchedHotel = hotelRepository.findHotelWithRoomsById(hotelId).
                orElseThrow(() -> new ResourceNotFoundException("Hotel not found with id: " + hotelId));

        return fetchedHotel.getRooms().stream()
                .map(RoomMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public boolean deleteRoomById(UUID id) {

        log.info("Deleting the room with id: {}", id);

        boolean exists = roomRepository.existsById(id);
        if(!exists) {
            throw new ResourceNotFoundException("Room not found with id: " + id);
        }

        roomRepository.deleteById(id);

        // Todo: Delete all future inventory for this room

        return true;
    }
}
