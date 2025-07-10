package com.rohankumar.easylodge.services.hotel;

import com.rohankumar.easylodge.dtos.hotel.HotelRequest;
import com.rohankumar.easylodge.dtos.hotel.HotelResponse;

import java.util.UUID;

public interface HotelService {

    HotelResponse createNewHotel(HotelRequest hotelRequest);

    HotelResponse getHotelById(UUID id);

    boolean updateHotelActivation(UUID id, boolean active);

    HotelResponse updateHotelById(UUID id, HotelRequest hotelRequest);

    boolean deleteHotelById(UUID id);
}
