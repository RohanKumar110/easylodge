package com.rohankumar.easylodge.services.inventory.impl;

import com.rohankumar.easylodge.dtos.hotel.HotelResponse;
import com.rohankumar.easylodge.dtos.hotel.price.HotelPriceResponse;
import com.rohankumar.easylodge.dtos.hotel.search.HotelSearchRequest;
import com.rohankumar.easylodge.dtos.wrapper.PaginationResponse;
import com.rohankumar.easylodge.entities.hotel.Hotel;
import com.rohankumar.easylodge.entities.hotel.HotelDailyPrice;
import com.rohankumar.easylodge.entities.inventory.Inventory;
import com.rohankumar.easylodge.entities.room.Room;
import com.rohankumar.easylodge.mappers.hotel.HotelMapper;
import com.rohankumar.easylodge.repositories.hotel.HotelDailyPriceRepository;
import com.rohankumar.easylodge.repositories.inventory.InventoryRepository;
import com.rohankumar.easylodge.services.inventory.InventoryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class InventoryServiceImpl implements InventoryService {

    @Value("${app.inventory.batch-size}")
    private Integer inventoryBatchSize;

    private final InventoryRepository inventoryRepository;
    private final HotelDailyPriceRepository dailyPriceRepository;

    @Override
    @Transactional
    public void initializeRoomInventoriesForYear(Room room) {

        // TODO: fix batch problem

        final Hotel hotel = room.getHotel();
        final UUID hotelId = hotel.getId();
        final UUID roomId = room.getId();

        LocalDate start = LocalDate.now();
        LocalDate end = start.plusYears(1);

        log.info("Initializing inventories for room {} in hotel {} ({} → {}).",
                roomId, hotelId, start, end);

        Set<LocalDate> existingDates = new HashSet<>(
                inventoryRepository.findExistingInventoryDates(hotelId, roomId, start, end.minusDays(1)));

        long created = 0;
        List<Inventory> batch = new ArrayList<>(inventoryBatchSize);

        for (LocalDate date = start; date.isBefore(end); date = date.plusDays(1)) {

            if (existingDates.contains(date))
                continue;

            batch.add(Inventory.builder()
                    .hotel(hotel)
                    .room(room)
                    .inventoryDate(date)
                    .bookedCount(0)
                    .reservedCount(0)
                    .totalRoomsCount(room.getTotalRoomsCount())
                    .surgeFactor(BigDecimal.ONE)
                    .price(room.getBasePrice())
                    .city(hotel.getContactInfo().getCity())
                    .closed(false)
                    .build());

            if (batch.size() == inventoryBatchSize) {
                inventoryRepository.saveAll(batch);
                batch.clear();
                created += inventoryBatchSize;
            }
        }

        if (!batch.isEmpty()) {
            inventoryRepository.saveAll(batch);
            created += batch.size();
        }

        changeInventoryAvailabilityByHotel(hotelId, false);

        log.info("Inventory initialized for room {}.", roomId);
        log.info("New Inventory created for room {}.", created);
    }

    @Override
    public PaginationResponse<HotelPriceResponse> searchHotels(HotelSearchRequest searchRequest) {

        log.info("Getting {} hotels for city: {}", searchRequest.getPageSize(), searchRequest.getCity());

        if (!searchRequest.getEndDate().isAfter(searchRequest.getStartDate())) {
            throw new IllegalArgumentException("Check-out date must be after check-in date");
        }

        Pageable pageable = PageRequest.of(searchRequest.getPageNo(), searchRequest.getPageSize());

        LocalDate now = LocalDate.now();
        LocalDate maxCachedDate = now.plusDays(90);
        Long requiredNights = ChronoUnit.DAYS.between(searchRequest.getStartDate(), searchRequest.getEndDate());

        Page<HotelPriceResponse> hotelResponsePage;
        if (!searchRequest.getStartDate().isBefore(now) &&
                !searchRequest.getEndDate().isAfter(maxCachedDate)) {

            log.info("Using HotelDailyPrice for pricing — search is within 90 days");
            hotelResponsePage = dailyPriceRepository.findAvailableHotelsFromDailyPrice(
                    searchRequest.getCity(),
                    searchRequest.getStartDate(),
                    searchRequest.getEndDate(),
                    requiredNights,
                    pageable
            );

        } else {

            log.info("Using Inventory for pricing — search is beyond 90 days");
            hotelResponsePage = inventoryRepository.searchHotelsWithAvailableInventory(
                    searchRequest.getCity(),
                    searchRequest.getStartDate(),
                    searchRequest.getEndDate(),
                    searchRequest.getRoomsCount(),
                    requiredNights,
                    pageable
            );
        }

        log.info("Hotels fetched successfully");
        log.info("Total Hotels Fetched: {}", hotelResponsePage.getTotalElements());

        return PaginationResponse.makeResponse(hotelResponsePage, hotelPriceResponse -> hotelPriceResponse);
    }

    @Override
    @Transactional
    public void changeInventoryAvailabilityByHotel(UUID hotelId, boolean closed) {

        log.info("Updating inventory availability for hotelId={} to closed={}", hotelId, closed);

        inventoryRepository.changeInventoryAvailabilityByHotel(hotelId, closed);

        log.info("Inventory availability updated successfully");
    }

    @Override
    @Transactional
    public void deleteAllRoomInventories(Room room) {

        log.info("Deleting inventories for room with id: {}", room.getId());

        inventoryRepository.deleteByRoom(room);

        log.info("Inventories deleted successfully for room with id: {}", room.getId());
    }
}
