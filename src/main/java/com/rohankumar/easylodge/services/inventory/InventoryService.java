package com.rohankumar.easylodge.services.inventory;

import com.rohankumar.easylodge.dtos.hotel.price.HotelPriceResponse;
import com.rohankumar.easylodge.dtos.hotel.search.HotelSearchRequest;
import com.rohankumar.easylodge.dtos.inventory.InventoryFilterRequest;
import com.rohankumar.easylodge.dtos.inventory.InventoryRequest;
import com.rohankumar.easylodge.dtos.inventory.InventoryResponse;
import com.rohankumar.easylodge.dtos.wrapper.PaginationResponse;
import com.rohankumar.easylodge.entities.room.Room;
import java.util.UUID;

public interface InventoryService {

    void initializeRoomInventoriesForYear(Room room);

    PaginationResponse<InventoryResponse> getAllInventoriesByRoom(UUID roomId, InventoryFilterRequest inventoryFilterRequest);

    PaginationResponse<HotelPriceResponse> searchHotels(HotelSearchRequest searchRequest);

    void changeInventoryAvailabilityByHotel(UUID hotelId, boolean closed);

    void updateInventoriesByRoom(UUID roomId, InventoryRequest inventoryRequest);

    void deleteAllRoomInventories(Room room);
}
