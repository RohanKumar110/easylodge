package com.rohankumar.easylodge.dtos.hotel;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class HotelRequest {

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
