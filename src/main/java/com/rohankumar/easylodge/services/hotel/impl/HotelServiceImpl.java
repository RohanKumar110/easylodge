package com.rohankumar.easylodge.services.hotel.impl;

import com.rohankumar.easylodge.dtos.hotel.HotelRequest;
import com.rohankumar.easylodge.dtos.hotel.HotelResponse;
import com.rohankumar.easylodge.dtos.hotel.info.HotelInfoRequest;
import com.rohankumar.easylodge.dtos.hotel.info.HotelInfoResponse;
import com.rohankumar.easylodge.dtos.hotel.report.HotelReportResponse;
import com.rohankumar.easylodge.dtos.room.RoomResponse;
import com.rohankumar.easylodge.entities.booking.Booking;
import com.rohankumar.easylodge.entities.common.ContactInfo;
import com.rohankumar.easylodge.entities.hotel.Hotel;
import com.rohankumar.easylodge.entities.inventory.Inventory;
import com.rohankumar.easylodge.entities.room.Room;
import com.rohankumar.easylodge.entities.user.User;
import com.rohankumar.easylodge.enums.booking.BookingStatus;
import com.rohankumar.easylodge.exceptions.ResourceNotFoundException;
import com.rohankumar.easylodge.mappers.hotel.HotelMapper;
import com.rohankumar.easylodge.mappers.room.RoomMapper;
import com.rohankumar.easylodge.repositories.booking.BookingRepository;
import com.rohankumar.easylodge.repositories.hotel.HotelRepository;
import com.rohankumar.easylodge.security.utils.SecurityUtils;
import com.rohankumar.easylodge.services.hotel.HotelService;
import com.rohankumar.easylodge.services.inventory.InventoryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class HotelServiceImpl implements HotelService {

    private final HotelRepository hotelRepository;
    private final InventoryService inventoryService;
    private final BookingRepository bookingRepository;

    @Override
    public HotelResponse createNewHotel(HotelRequest hotelRequest) {

        log.info("Creating the hotel with name: {}", hotelRequest.getName());

        Hotel hotelToSave = HotelMapper.toEntity(hotelRequest);
        hotelToSave.setActive(false);

        hotelToSave.setOwner(SecurityUtils.getCurrentUser());
        Hotel savedHotel = hotelRepository.save(hotelToSave);

        log.info("Hotel created successfully with id: {}", savedHotel.getId());

        return HotelMapper.toResponse(savedHotel);
    }

    @Override
    public HotelResponse getHotelById(UUID id) {

        log.info("Getting the hotel with id: {}", id);

        Hotel fetchedHotel = hotelRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Hotel not found with id: " + id));

        log.info("Hotel fetched successfully with id: {}", fetchedHotel.getId());
        return HotelMapper.toResponse(fetchedHotel);
    }

    @Override
    public HotelInfoResponse getHotelInfo(UUID id, HotelInfoRequest hotelInfoRequest) {

        log.info("Getting the hotel info with id: {}", id);

        Hotel fetchedHotel = hotelRepository.findHotelWithRoomsById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Hotel not found with id: " + id));

        List<RoomResponse> rooms = fetchedHotel.getRooms().stream()
                .map(RoomMapper::toResponse)
                .toList();

        HotelInfoResponse hotelInfoResponse = new HotelInfoResponse();
        hotelInfoResponse.setHotel(HotelMapper.toResponse(fetchedHotel));
        hotelInfoResponse.setRooms(rooms);

        log.info("Hotel info fetched successfully with id: {}", fetchedHotel.getId());

        return hotelInfoResponse;
    }

    @Override
    public HotelReportResponse getHotelReportById(UUID id, LocalDate startDate, LocalDate endDate) {

        log.info("Getting the hotel report for hotel with id: {}", id);

        if (startDate == null) {

            log.info("Start date is null. Defaulting to a 30-day report ending on {}.", endDate);

            startDate = LocalDate.now().minusMonths(1);
            endDate = LocalDate.now();
        }

        log.info("Generating report from {} to {}.", startDate, endDate);

        log.info("Getting the hotel");
        Hotel fetchedHotel = hotelRepository.findHotelWithRoomsById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Hotel not found with id: " + id));

        LocalDateTime startDateTime = startDate.atTime(LocalTime.MIN);
        LocalDateTime endDateTime = endDate.atTime(LocalTime.MAX);

        log.info("Getting the hotel report");
        HotelReportResponse hotelReportResponse = bookingRepository.findHotelReportByStatusAndDateRange(
                fetchedHotel, BookingStatus.CONFIRMED, startDateTime, endDateTime);

        log.info("Hotel report fetched successfully with id: {}", fetchedHotel.getId());

        return hotelReportResponse;
    }

    @Override
    public List<HotelResponse> getAllHotels() {

        User  user = SecurityUtils.getCurrentUser();
        log.info("Getting all the hotels for user: {}", user.getId());

        List<Hotel> hotels = hotelRepository.findByOwner(user);

        log.info("Hotels fetched successfully");
        log.info("Total hotels found: {}", hotels.size());

        return hotels.stream()
                .map(HotelMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional
    public boolean updateHotelActivation(UUID id, boolean active) {

        log.info("Updating the hotel with id: {} activation to {}", id, active);

        Hotel fetchedHotel = hotelRepository.findHotelWithRoomsById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Hotel not found with id: " + id));

        fetchedHotel.setActive(active);

        hotelRepository.save(fetchedHotel);

        log.info("Hotel activation updated successfully with id: {}", fetchedHotel.getId());

        if (fetchedHotel.getActive()) {
            Optional.ofNullable(fetchedHotel.getRooms())
                    .filter(rooms -> !rooms.isEmpty())
                    .ifPresentOrElse(
                            rooms -> rooms.forEach(inventoryService::initializeRoomInventoriesForYear),
                            () -> log.info("No rooms available for hotel with id: {}. No inventory created.", fetchedHotel.getId())
                    );
        } else {
            inventoryService.changeInventoryAvailabilityByHotel(fetchedHotel.getId(), true);
        }

        return true;
    }

    @Override
    public HotelResponse updateHotelById(UUID id, HotelRequest hotelRequest) {

        log.info("Updating the hotel with Id: {}", id);

        Hotel existingHotel = hotelRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Hotel not found with id: " + id));

        if (hotelRequest.getName() != null) existingHotel.setName(hotelRequest.getName());
        if (hotelRequest.getActive() != null) existingHotel.setActive(hotelRequest.getActive());
        if (hotelRequest.getImages() != null) existingHotel.setImages(hotelRequest.getImages());
        if (hotelRequest.getAmenities() != null) existingHotel.setAmenities(hotelRequest.getAmenities());

        ContactInfo contact = existingHotel.getContactInfo();
        if (contact == null) contact = new ContactInfo();

        if (hotelRequest.getEmail() != null) contact.setEmail(hotelRequest.getEmail());
        if (hotelRequest.getPhone() != null) contact.setPhone(hotelRequest.getPhone());
        if (hotelRequest.getCity() != null) contact.setCity(hotelRequest.getCity());
        if (hotelRequest.getCoordinates() != null) contact.setCoordinates(hotelRequest.getCoordinates());
        if (hotelRequest.getAddress() != null) contact.setAddress(hotelRequest.getAddress());

        existingHotel.setContactInfo(contact);

        Hotel updatedHotel = hotelRepository.save(existingHotel);

        log.info("Hotel updated successfully with Id: {}", updatedHotel.getId());

        return HotelMapper.toResponse(updatedHotel);
    }

    @Override
    @Transactional
    public boolean deleteHotelById(UUID id) {

        log.info("Deleting the hotel with id: {}", id);

        Hotel fetchedHotel = hotelRepository.findHotelWithRoomsById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Hotel not found with id: " + id));

        fetchedHotel.getRooms().forEach(inventoryService::deleteAllRoomInventories);

        hotelRepository.delete(fetchedHotel);

        log.info("Hotel deleted successfully with Id: {}", id);

        return true;
    }
}
