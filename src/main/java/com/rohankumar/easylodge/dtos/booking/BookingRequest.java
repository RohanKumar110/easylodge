package com.rohankumar.easylodge.dtos.booking;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import java.time.LocalDate;
import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BookingRequest {

    @NotNull(message = "Hotel Id is required")
    private UUID hotelId;

    @NotNull(message = "Room Id is required")
    private UUID roomId;

    @NotNull(message = "Number of rooms is required")
    @Min(value = 1, message = "Number of rooms must be greater than zero")
    private Integer numberOfRooms;

    @NotNull(message = "Check-in date is required")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate checkInDate;

    @NotNull(message = "Check-out date is required")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate checkOutDate;
}
