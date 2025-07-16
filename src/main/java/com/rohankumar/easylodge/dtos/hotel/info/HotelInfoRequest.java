package com.rohankumar.easylodge.dtos.hotel.info;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.time.LocalDate;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class HotelInfoRequest {

    private LocalDate startDate;
    private LocalDate endDate;
}
