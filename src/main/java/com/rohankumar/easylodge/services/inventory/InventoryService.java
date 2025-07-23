package com.rohankumar.easylodge.services.inventory;

import com.rohankumar.easylodge.dtos.hotel.price.HotelPriceResponse;
import com.rohankumar.easylodge.dtos.hotel.search.HotelSearchRequest;
import com.rohankumar.easylodge.dtos.inventory.InventoryRequest;
import com.rohankumar.easylodge.dtos.inventory.InventoryResponse;
import com.rohankumar.easylodge.dtos.wrapper.PaginationResponse;
import com.rohankumar.easylodge.entities.hotel.Hotel;
import com.rohankumar.easylodge.entities.inventory.Inventory;
import com.rohankumar.easylodge.entities.room.Room;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public interface InventoryService {

    void initializeRoomInventoriesForYear(Room room);

    PaginationResponse<InventoryResponse> getAllInventoriesByRoom(UUID roomId, InventoryRequest inventoryRequest);

    PaginationResponse<HotelPriceResponse> searchHotels(HotelSearchRequest searchRequest);

    void changeInventoryAvailabilityByHotel(UUID hotelId, boolean closed);

    void deleteAllRoomInventories(Room room);
}
