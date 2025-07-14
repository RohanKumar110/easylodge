package com.rohankumar.easylodge.dtos.hotel.search;

import com.rohankumar.easylodge.utilities.constants.AppConstants;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class HotelSearchRequest {

    private String city;
    private LocalDate startDate;
    private LocalDate endDate;
    private Integer roomsCount;
    private Integer pageNo = AppConstants.PAGE_NO;
    private Integer pageSize = AppConstants.PAGE_SIZE;
}
