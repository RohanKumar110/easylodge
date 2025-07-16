package com.rohankumar.easylodge.dtos.room;

import lombok.*;
import java.math.BigDecimal;
import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RoomResponse {

    private UUID id;
    private String type;
    private BigDecimal price;
    private String[] images;
    private String[] amenities;
    private Integer totalRoomsCount;
    private Integer capacity;
}
