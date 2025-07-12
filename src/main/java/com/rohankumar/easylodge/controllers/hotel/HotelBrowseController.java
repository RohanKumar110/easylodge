package com.rohankumar.easylodge.controllers.hotel;

import com.rohankumar.easylodge.dtos.hotel.HotelResponse;
import com.rohankumar.easylodge.dtos.hotel.search.HotelSearchRequest;
import com.rohankumar.easylodge.dtos.wrapper.ApiResponse;
import com.rohankumar.easylodge.dtos.wrapper.PaginationResponse;
import com.rohankumar.easylodge.services.inventory.InventoryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/hotels")
public class HotelBrowseController {

    private final InventoryService inventoryService;

    @GetMapping("/search")
    public ResponseEntity<ApiResponse<PaginationResponse<HotelResponse>>> searchHotels(
            @ModelAttribute HotelSearchRequest searchRequest) {

        // TODO: check for global response handler
        log.info("Attempting to search hotels for city: {}", searchRequest.getCity());
        PaginationResponse<HotelResponse> paginationResponse = inventoryService.searchHotels(searchRequest);

        return ResponseEntity.status(HttpStatus.OK).body(
                ApiResponse.success(HttpStatus.OK.value(), "Hotels Fetched Successfully", paginationResponse));
    }
}
