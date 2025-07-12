package com.rohankumar.easylodge.services.inventory;

import com.rohankumar.easylodge.entities.room.Room;

public interface InventoryService {

    void initializeRoomInventoriesForYear(Room room);

    void deleteAllRoomInventories(Room room);
}
