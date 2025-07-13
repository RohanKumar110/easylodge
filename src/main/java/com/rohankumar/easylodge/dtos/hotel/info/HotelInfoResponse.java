package com.rohankumar.easylodge.dtos.hotel.info;

import com.rohankumar.easylodge.dtos.hotel.HotelResponse;
import com.rohankumar.easylodge.dtos.room.RoomResponse;
import lombok.*;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class HotelInfoResponse {

    private HotelResponse hotel;
    private List<RoomResponse> rooms;
}
