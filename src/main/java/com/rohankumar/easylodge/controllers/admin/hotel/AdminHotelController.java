package com.rohankumar.easylodge.controllers.admin.hotel;

import com.rohankumar.easylodge.dtos.booking.BookingResponse;
import com.rohankumar.easylodge.dtos.hotel.HotelRequest;
import com.rohankumar.easylodge.dtos.hotel.HotelResponse;
import com.rohankumar.easylodge.dtos.hotel.report.HotelReportResponse;
import com.rohankumar.easylodge.dtos.wrapper.ApiResponse;
import com.rohankumar.easylodge.enums.booking.BookingStatus;
import com.rohankumar.easylodge.services.booking.BookingService;
import com.rohankumar.easylodge.services.hotel.HotelService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/admin/hotels")
public class AdminHotelController {

    private final HotelService hotelService;
    private final BookingService bookingService;

    @PostMapping
    public ResponseEntity<ApiResponse<HotelResponse>> createNewHotel(
            @Valid @RequestBody HotelRequest hotelRequest) {

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

    @GetMapping("/{hotelId}/reports")
    public ResponseEntity<ApiResponse<HotelReportResponse>> getHotelReportById(
            @PathVariable UUID hotelId,
            @RequestParam BookingStatus bookingStatus,
            @RequestParam(required = false) LocalDate startDate,
            @RequestParam(required = false) LocalDate endDate) {

        log.info("Attempting to get hotel report with id: {}", hotelId);
        HotelReportResponse hotelReportResponse = hotelService
                .getHotelReportById(hotelId, bookingStatus, startDate, endDate);
        return ResponseEntity.status(HttpStatus.OK).body(
                ApiResponse.success(HttpStatus.OK.value(), "Hotel Report Fetched Successfully", hotelReportResponse));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<HotelResponse>>> getAllHotels() {

        log.info("Attempting to get all hotels");
        List<HotelResponse> hotelResponseList = hotelService.getAllHotels();
        return ResponseEntity.status(HttpStatus.OK).body(
                ApiResponse.success(HttpStatus.OK.value(), "Hotels Fetched Successfully", hotelResponseList));
    }

    @GetMapping("/{hotelId}/bookings")
    public ResponseEntity<ApiResponse<List<BookingResponse>>> getAllBookingsByHotelId(
            @PathVariable UUID hotelId) {

        log.info("Attempting to get all bookings for hotel with id: {}", hotelId);
        List<BookingResponse> bookingResponseList = bookingService.getAllBookingsByHotelId(hotelId);
        return ResponseEntity.status(HttpStatus.OK).body(
                ApiResponse.success(HttpStatus.OK.value(), "Bookings Fetched Successfully", bookingResponseList));
    }

    @PatchMapping("/{hotelId}/activation")
    public ResponseEntity<ApiResponse<Void>> updateHotelActivationById(
            @PathVariable UUID hotelId, @Valid @RequestParam boolean active) {

        log.info("Attempting to update hotel activation with id: {}", hotelId);
        hotelService.updateHotelActivation(hotelId, active);
        return ResponseEntity.status(HttpStatus.OK).body(
                ApiResponse.success(HttpStatus.OK.value(), "Hotel Activation Updated Successfully"));
    }

    @PutMapping("/{hotelId}")
    public ResponseEntity<ApiResponse<HotelResponse>> updateHotelById(
            @PathVariable UUID hotelId, @Valid @RequestBody HotelRequest hotelRequest) {

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
