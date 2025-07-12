package com.rohankumar.easylodge.services.inventory.impl;

import com.rohankumar.easylodge.dtos.hotel.HotelResponse;
import com.rohankumar.easylodge.dtos.hotel.search.HotelSearchRequest;
import com.rohankumar.easylodge.dtos.wrapper.PaginationResponse;
import com.rohankumar.easylodge.entities.hotel.Hotel;
import com.rohankumar.easylodge.entities.inventory.Inventory;
import com.rohankumar.easylodge.entities.room.Room;
import com.rohankumar.easylodge.mappers.hotel.HotelMapper;
import com.rohankumar.easylodge.repositories.inventory.InventoryRepository;
import com.rohankumar.easylodge.services.inventory.InventoryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class InventoryServiceImpl implements InventoryService {

    @Value("${app.inventory.batch-size}")
    private Integer inventoryBatchSize;

    private final InventoryRepository inventoryRepository;

    @Override
    public void initializeRoomInventoriesForYear(Room room) {

        // TODO: Fix Batch Issue

        LocalDate todayDate = LocalDate.now();
        LocalDate endDate = todayDate.plusYears(1);

        log.info("Initializing inventory for room with id: {} in hotel with id: {} for the next year",
                room.getId(), room.getHotel().getId());

        int count = 0;
        List<Inventory> inventoryBatch = new ArrayList<>();

        while(!todayDate.isEqual(endDate)) {

            Inventory inventory = Inventory.builder()
                    .hotel(room.getHotel())
                    .room(room)
                    .bookedCount(0)
                    .city(room.getHotel().getContactInfo().getCity())
                    .inventoryDate(todayDate)
                    .price(room.getBasePrice())
                    .surgeFactor(BigDecimal.ONE)
                    .totalRoomsCount(room.getTotalRoomsCount())
                    .closed(false)
                    .build();

            inventoryBatch.add(inventory);

            count++;
            todayDate = todayDate.plusDays(1);

            if(count % inventoryBatchSize == 0) {

                inventoryRepository.saveAll(inventoryBatch);
                inventoryRepository.flush();
                inventoryBatch.clear();
            }
        }

        if (!inventoryBatch.isEmpty()) {
            inventoryRepository.saveAll(inventoryBatch);
            inventoryRepository.flush();
        }

        log.info("Inventory initialized successfully for room with id: {}", room.getId());
        log.info("Total inventories created: {}", inventoryBatch.size());
    }

    @Override
    public void deleteAllRoomInventories(Room room) {

        log.info("Deleting inventories for room with id: {}", room.getId());

        inventoryRepository.deleteByRoom(room);

        log.info("Inventories deleted successfully for room with id: {}", room.getId());
    }

    @Override
    public PaginationResponse<HotelResponse> searchHotels(HotelSearchRequest searchRequest) {

        log.info("Getting {} hotels for city: {}", searchRequest.getPageSize(), searchRequest.getCity());

        if (!searchRequest.getEndDate().isAfter(searchRequest.getStartDate())) {
            throw new IllegalArgumentException("Check-out date must be after check-in date");
        }

        Pageable pageable = PageRequest.of(searchRequest.getPageNo(), searchRequest.getPageSize());

        Long requiredNights = ChronoUnit.DAYS.between(searchRequest.getStartDate(), searchRequest.getEndDate());

        Page<Hotel> hotelPage = inventoryRepository.searchHotelsWithAvailableInventory(
                searchRequest.getCity(),
                searchRequest.getStartDate(),
                searchRequest.getEndDate(),
                searchRequest.getRoomsCount(),
                requiredNights,
                pageable
        );

        log.info("Hotels fetched successfully");
        log.info("Total Hotels Fetched: {}", hotelPage.getTotalElements());

        return PaginationResponse.makeResponse(hotelPage, HotelMapper::toResponse);
    }
}
