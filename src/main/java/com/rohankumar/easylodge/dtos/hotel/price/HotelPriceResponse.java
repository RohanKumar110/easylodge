package com.rohankumar.easylodge.dtos.hotel.price;

import com.rohankumar.easylodge.dtos.hotel.HotelResponse;
import com.rohankumar.easylodge.entities.hotel.Hotel;
import com.rohankumar.easylodge.mappers.hotel.HotelMapper;
import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HotelPriceResponse {

    private HotelResponse hotel;
    private BigDecimal price;

    public HotelPriceResponse(Hotel hotel, BigDecimal price) {

        this.hotel = HotelMapper.toResponse(hotel);
        this.price = price;
    }

    public HotelPriceResponse(Hotel hotel, Double avgPrice) {

        this.hotel = HotelMapper.toResponse(hotel);
        this.price = avgPrice != null ? BigDecimal.valueOf(avgPrice) : BigDecimal.ZERO;
    }
}
