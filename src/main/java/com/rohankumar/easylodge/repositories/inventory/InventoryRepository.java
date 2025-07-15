package com.rohankumar.easylodge.repositories.inventory;

import com.rohankumar.easylodge.dtos.hotel.price.HotelPriceResponse;
import com.rohankumar.easylodge.entities.hotel.Hotel;
import com.rohankumar.easylodge.entities.inventory.Inventory;
import com.rohankumar.easylodge.entities.room.Room;
import jakarta.persistence.LockModeType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Repository
public interface InventoryRepository extends JpaRepository<Inventory, UUID> {

    @Modifying
    @Query("DELETE FROM Inventory i WHERE i.room = :room")
    void deleteByRoom(Room room);

    @Query("""
        SELECT new com.rohankumar.easylodge.dtos.hotel.price.HotelPriceResponse(i.hotel, MIN(i.price))
        FROM Inventory i
        WHERE i.city = :city
          AND (i.inventoryDate >= :startDate AND i.inventoryDate < :endDate)
          AND (i.totalRoomsCount - i.bookedCount - i.reservedCount) >= :roomsCount
          AND i.closed = FALSE
        GROUP BY i.hotel, i.room
        HAVING COUNT(i.inventoryDate) = :requiredNights
    """)
    Page<HotelPriceResponse> searchHotelsWithAvailableInventory(
            String city,
            LocalDate startDate,
            LocalDate endDate,
            Integer roomsCount,
            Long requiredNights,
            Pageable pageable
    );

    @Modifying
    @Query("""
        UPDATE Inventory i
        SET i.closed = :closed
        WHERE i.hotel.id = :hotelId
    """)
    void changeInventoryAvailabilityByHotel(UUID hotelId, boolean closed);

    @Query("""
        SELECT i.inventoryDate
        FROM Inventory i
        WHERE i.hotel.id = :hotelId
          AND i.room.id  = :roomId
          AND i.inventoryDate BETWEEN :start AND :end
    """)
    List<LocalDate> findExistingInventoryDates(UUID hotelId, UUID roomId, LocalDate start, LocalDate end);


    @Query("""
        SELECT i
        FROM Inventory i
        WHERE i.room = :room
             AND (i.inventoryDate >= :startDate AND i.inventoryDate < :endDate)
             AND (i.totalRoomsCount - i.bookedCount - i.reservedCount) >= :roomsCount
             AND i.closed = FALSE
    """)
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    List<Inventory> findAndLockAvailableInventory(Room room, LocalDate startDate, LocalDate endDate, Integer roomsCount);

    @Query("""
        SELECT i
        FROM Inventory i
            WHERE i.hotel = :hotel
                AND (i.inventoryDate >= :startDate AND i.inventoryDate <= :endDate)
    """)
    List<Inventory> findByHotelAndDateBetween(Hotel hotel, LocalDate startDate, LocalDate endDate);
}
