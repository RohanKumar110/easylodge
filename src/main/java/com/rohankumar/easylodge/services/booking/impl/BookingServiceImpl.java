package com.rohankumar.easylodge.services.booking.impl;

import com.rohankumar.easylodge.dtos.booking.BookingRequest;
import com.rohankumar.easylodge.dtos.booking.BookingResponse;
import com.rohankumar.easylodge.entities.booking.Booking;
import com.rohankumar.easylodge.entities.hotel.Hotel;
import com.rohankumar.easylodge.entities.inventory.Inventory;
import com.rohankumar.easylodge.entities.room.Room;
import com.rohankumar.easylodge.enums.booking.BookingStatus;
import com.rohankumar.easylodge.exceptions.BadRequestException;
import com.rohankumar.easylodge.exceptions.ResourceNotFoundException;
import com.rohankumar.easylodge.mappers.booking.BookingMapper;
import com.rohankumar.easylodge.repositories.booking.BookingRepository;
import com.rohankumar.easylodge.repositories.hotel.HotelRepository;
import com.rohankumar.easylodge.repositories.inventory.InventoryRepository;
import com.rohankumar.easylodge.repositories.room.RoomRepository;
import com.rohankumar.easylodge.repositories.user.UserRepository;
import com.rohankumar.easylodge.services.booking.BookingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {

    private final RoomRepository roomRepository;
    private final UserRepository userRepository;
    private final HotelRepository hotelRepository;
    private final BookingRepository bookingRepository;
    private final InventoryRepository inventoryRepository;

    @Override
    @Transactional
    public BookingResponse initializeBooking(BookingRequest bookingRequest) {

        log.info("Initializing booking for hotel [{}] and room [{}]",  bookingRequest.getHotelId(), bookingRequest.getRoomId());

        log.info("Finding hotel with id: {}", bookingRequest.getHotelId());
        Hotel fetchedHotel = hotelRepository.findById(bookingRequest.getHotelId())
                .orElseThrow(() -> new ResourceNotFoundException("Hotel not found with id: " + bookingRequest.getHotelId()));

        log.info("Finding room with id: {}", bookingRequest.getRoomId());
        Room fetchedRoom = roomRepository.findById(bookingRequest.getRoomId())
                .orElseThrow(() -> new ResourceNotFoundException("Room not found with id: " + bookingRequest.getRoomId()));

        List<Inventory> inventories = inventoryRepository.findAndLockAvailableInventory(
                fetchedRoom, bookingRequest.getCheckInDate(), bookingRequest.getCheckOutDate(), bookingRequest.getNumberOfRooms());

        long daysCount = ChronoUnit.DAYS.between(bookingRequest.getCheckInDate(), bookingRequest.getCheckOutDate());

        if(inventories.size() != daysCount) {
            throw new BadRequestException("Room is not available anymore");
        }

        inventories.forEach(inventory ->
            inventory.setReservedCount(inventory.getReservedCount() + bookingRequest.getNumberOfRooms()));

        inventoryRepository.saveAll(inventories);

        // TODO: calculate dynamic amount

        Booking booking = Booking.builder()
                .status(BookingStatus.RESERVED)
                .amount(BigDecimal.TEN)
                .hotel(fetchedHotel)
                .room(fetchedRoom)
                .user(userRepository.findById(UUID.fromString("8b3b2617-4a16-4722-9eb6-7f56b6ba48e0")).get())
                .numberOfRooms(bookingRequest.getNumberOfRooms())
                .checkInDate(bookingRequest.getCheckInDate())
                .checkOutDate(bookingRequest.getCheckOutDate())
                .build();

        log.info("Creating booking for user: {}", booking.getUser().getId());
        Booking savedBooking = bookingRepository.save(booking);
        log.info("Booking created successfully with id: {}", savedBooking.getId());

        return BookingMapper.toResponse(savedBooking);
    }
}
