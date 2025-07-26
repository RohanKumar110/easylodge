package com.rohankumar.easylodge.dtos.hotel;

import lombok.*;
import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HotelResponse {

    private UUID id;
    private String name;
    private Boolean active;
    private String[] images;
    private String[] amenities;
    private String email;
    private String phone;
    private String city;
    private String coordinates;
    private String address;
}
