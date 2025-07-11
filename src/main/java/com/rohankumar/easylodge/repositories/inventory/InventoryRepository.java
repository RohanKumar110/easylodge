package com.rohankumar.easylodge.repositories.inventory;

import com.rohankumar.easylodge.entities.inventory.Inventory;
import com.rohankumar.easylodge.entities.room.Room;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.time.LocalDate;
import java.util.UUID;

@Repository
public interface InventoryRepository extends JpaRepository<Inventory, UUID> {

    @Modifying
    @Query("DELETE FROM Inventory i WHERE i.room = :room AND i.inventoryDate > :cutoffDate")
    void deleteByRoomAndInventoryDate(Room room, LocalDate cutoffDate);
}
