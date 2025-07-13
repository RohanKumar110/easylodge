package com.rohankumar.easylodge.services.inventory;

import com.rohankumar.easylodge.dtos.hotel.HotelResponse;
import com.rohankumar.easylodge.dtos.hotel.search.HotelSearchRequest;
import com.rohankumar.easylodge.dtos.wrapper.PaginationResponse;
import com.rohankumar.easylodge.entities.room.Room;
import java.util.UUID;

public interface InventoryService {

    void initializeRoomInventoriesForYear(Room room);

    PaginationResponse<HotelResponse> searchHotels(HotelSearchRequest searchRequest);

    void changeInventoryAvailabilityByHotel(UUID hotelId, boolean closed);

    void deleteAllRoomInventories(Room room);
}
