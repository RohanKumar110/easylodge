package com.rohankumar.easylodge.dtos.room;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.math.BigDecimal;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RoomRequest {

    private String type;
    private BigDecimal basePrice;
    private String[] images;
    private String[] amenities;
    private Integer totalRoomsCount;
    private Integer capacity;
}
