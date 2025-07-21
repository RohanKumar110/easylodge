package com.rohankumar.easylodge.dtos.hotel.search;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.rohankumar.easylodge.utils.constants.AppConstants;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
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

    @NotBlank(message = "City is required")
    @Pattern(
            regexp = "^[\\p{L}0-9]+(?:[\\s'\\-.][\\p{L}0-9]+)*$",
            message = "City name can only contain letters, numbers, spaces, hyphens, apostrophes, and periods"
    )
    private String city;

    @NotNull(message = "Start date is required")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate startDate;

    @NotNull(message = "End date is required")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate endDate;

    @NotNull(message = "Room count is required")
    @Min(value = 1, message = "Room count must be greater than zero")
    private Integer roomsCount;

    private Integer pageNo = AppConstants.PAGE_NO;
    private Integer pageSize = AppConstants.PAGE_SIZE;
}
