package com.rohankumar.easylodge.services.hotel;

import com.rohankumar.easylodge.dtos.hotel.HotelRequest;
import com.rohankumar.easylodge.dtos.hotel.HotelResponse;

import java.util.UUID;

public interface HotelService {

    HotelResponse createNewHotel(HotelRequest hotelRequest);

    HotelResponse getHotelById(UUID id);

    HotelResponse updateHotelById(UUID id, HotelRequest hotelRequest);

    Boolean deleteHotelById(UUID id);
}
