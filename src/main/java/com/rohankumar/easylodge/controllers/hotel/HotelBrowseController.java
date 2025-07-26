package com.rohankumar.easylodge.controllers.hotel;

import com.rohankumar.easylodge.dtos.hotel.info.HotelInfoRequest;
import com.rohankumar.easylodge.dtos.hotel.info.HotelInfoResponse;
import com.rohankumar.easylodge.dtos.hotel.price.HotelPriceResponse;
import com.rohankumar.easylodge.dtos.hotel.search.HotelSearchRequest;
import com.rohankumar.easylodge.dtos.wrapper.ApiResponse;
import com.rohankumar.easylodge.dtos.wrapper.PaginationResponse;
import com.rohankumar.easylodge.services.hotel.HotelService;
import com.rohankumar.easylodge.services.inventory.InventoryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.UUID;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/hotels")
public class HotelBrowseController {

    private final HotelService hotelService;
    private final InventoryService inventoryService;

    @GetMapping("/search")
    public ResponseEntity<ApiResponse<PaginationResponse<HotelPriceResponse>>> searchHotels(
            @Valid @ModelAttribute HotelSearchRequest searchRequest) {

        // TODO: check for global response handler
        log.info("Attempting to search hotels for city: {}", searchRequest.getCity());
        PaginationResponse<HotelPriceResponse> paginationResponse = inventoryService.searchHotels(searchRequest);
        return ResponseEntity.status(HttpStatus.OK).body(
                ApiResponse.success(HttpStatus.OK.value(), "Hotels Fetched Successfully", paginationResponse));
    }

    @PostMapping("/{hotelId}/info")
    public ResponseEntity<ApiResponse<HotelInfoResponse>> getHotelInfo(
            @PathVariable UUID hotelId,
            @Valid @RequestBody HotelInfoRequest hotelInfoRequest) {

        log.info("Attempting to get hotel info for hotel: {}", hotelId);
        HotelInfoResponse hotelInfoResponse = hotelService.getHotelInfo(hotelId, hotelInfoRequest);
        return ResponseEntity.status(HttpStatus.OK).body(
                ApiResponse.success(HttpStatus.OK.value(), "Hotel Info Fetched Successfully", hotelInfoResponse));
    }
}
