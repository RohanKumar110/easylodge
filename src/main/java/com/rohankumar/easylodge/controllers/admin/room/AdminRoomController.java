package com.rohankumar.easylodge.controllers.admin.room;

import com.rohankumar.easylodge.dtos.room.RoomRequest;
import com.rohankumar.easylodge.dtos.room.RoomResponse;
import com.rohankumar.easylodge.dtos.wrapper.ApiResponse;
import com.rohankumar.easylodge.services.room.RoomService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.coyote.Response;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/admin/hotels/{hotelId}/rooms")
public class AdminRoomController {

    private final RoomService roomService;

    @PostMapping
    public ResponseEntity<ApiResponse<RoomResponse>> createNewRoom(
            @PathVariable UUID hotelId, @Valid @RequestBody RoomRequest roomRequest) {

        log.info("Attempting to create a room with type: {}", roomRequest.getType());
        RoomResponse roomResponse = roomService.createNewRoom(hotelId, roomRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(
                ApiResponse.success(HttpStatus.CREATED.value(), "Room Created Successfully", roomResponse));
    }

    @GetMapping("/{roomId}")
    public ResponseEntity<ApiResponse<RoomResponse>> getRoomById(
            @PathVariable UUID hotelId, @PathVariable UUID roomId) {

        log.info("Attempting to get room with id: {}", roomId);
        RoomResponse roomResponse = roomService.getRoomById(roomId);
        return ResponseEntity.status(HttpStatus.OK).body(
                ApiResponse.success(HttpStatus.OK.value(), "Room Fetched Successfully", roomResponse));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<RoomResponse>>> getAllRoomsByHotelId(@PathVariable UUID hotelId) {

        log.info("Attempting to get all rooms for hotel with id: {}", hotelId);
        List<RoomResponse> rooms = roomService.getAllRoomsByHotelId(hotelId);
        return ResponseEntity.status(HttpStatus.OK).body(
                ApiResponse.success(HttpStatus.OK.value(), "Rooms Fetched Successfully", rooms));
    }

    @DeleteMapping("/{roomId}")
    public ResponseEntity<ApiResponse<Void>> deleteRoomById(
            @PathVariable UUID hotelId, @PathVariable UUID roomId) {

        log.info("Attempting to delete room with id: {}", roomId);
        roomService.deleteRoomById(roomId);
        return ResponseEntity.status(HttpStatus.OK).body(
                ApiResponse.success(HttpStatus.OK.value(), "Room Deleted Successfully"));
    }
}
