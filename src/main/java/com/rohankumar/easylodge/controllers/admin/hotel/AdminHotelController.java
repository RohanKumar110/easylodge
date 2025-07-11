package com.rohankumar.easylodge.controllers.admin.hotel;

import com.rohankumar.easylodge.dtos.hotel.HotelRequest;
import com.rohankumar.easylodge.dtos.hotel.HotelResponse;
import com.rohankumar.easylodge.dtos.wrapper.ApiResponse;
import com.rohankumar.easylodge.services.hotel.HotelService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.UUID;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/admin/hotels")
public class AdminHotelController {

    private final HotelService hotelService;

    @PostMapping
    public ResponseEntity<ApiResponse<HotelResponse>> createNewHotel(@RequestBody HotelRequest hotelRequest) {

        log.info("Attempting to create hotel with name: {}", hotelRequest.getName());
        HotelResponse hotelResponse = hotelService.createNewHotel(hotelRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(
                ApiResponse.success(HttpStatus.CREATED.value(), "Hotel Created Successfully", hotelResponse));
    }

    @GetMapping("/{hotelId}")
    public ResponseEntity<ApiResponse<HotelResponse>> getHotelById(@PathVariable UUID hotelId) {

        log.info("Attempting to get hotel with id: {}", hotelId);
        HotelResponse hotelResponse =  hotelService.getHotelById(hotelId);
        return ResponseEntity.status(HttpStatus.OK).body(
                ApiResponse.success(HttpStatus.OK.value(), "Hotel Fetched Successfully", hotelResponse));
    }

    @PatchMapping("/{hotelId}/activation")
    public ResponseEntity<ApiResponse<Void>> updateHotelActivationById(
            @PathVariable UUID hotelId, @RequestParam boolean active) {

        log.info("Attempting to update hotel activation with id: {}", hotelId);
        hotelService.updateHotelActivation(hotelId, active);
        return ResponseEntity.status(HttpStatus.OK).body(
                ApiResponse.success(HttpStatus.OK.value(), "Hotel Activation Updated Successfully"));
    }

    @PutMapping("/{hotelId}")
    public ResponseEntity<ApiResponse<HotelResponse>> updateHotelById(
            @PathVariable UUID hotelId, @RequestBody HotelRequest hotelRequest) {

        log.info("Attempting to update hotel with id: {}", hotelId);
        HotelResponse hotelResponse = hotelService.updateHotelById(hotelId, hotelRequest);
        return ResponseEntity.status(HttpStatus.OK).body(
                ApiResponse.success(HttpStatus.OK.value(), "Hotel Updated Successfully", hotelResponse));
    }

    @DeleteMapping("/{hotelId}")
    public ResponseEntity<ApiResponse<Void>> deleteHotelById(@PathVariable UUID hotelId) {

        log.info("Attempting to delete hotel with id: {}", hotelId);
        hotelService.deleteHotelById(hotelId);
        return ResponseEntity.status(HttpStatus.OK).body(
                ApiResponse.success(HttpStatus.NO_CONTENT.value(), "Hotel Deleted Successfully"));
    }
}
