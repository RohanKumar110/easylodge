package com.rohankumar.easylodge.services.inventory;

import com.rohankumar.easylodge.dtos.hotel.HotelResponse;
import com.rohankumar.easylodge.dtos.hotel.search.HotelSearchRequest;
import com.rohankumar.easylodge.dtos.wrapper.PaginationResponse;
import com.rohankumar.easylodge.entities.room.Room;

public interface InventoryService {

    void initializeRoomInventoriesForYear(Room room);

    void deleteAllRoomInventories(Room room);

    PaginationResponse<HotelResponse> searchHotels(HotelSearchRequest searchRequest);
}
