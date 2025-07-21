package com.rohankumar.easylodge.dtos.room;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RoomRequest {

    @NotBlank(message = "Type is required")
    private String type;

    @NotNull(message = "Base price is required")
    @DecimalMin(value = "0.0", inclusive = false, message = "Base price must be greater than 0")
    private BigDecimal basePrice;

    @NotNull(message = "Images are required")
    @Size(max = 10, message = "You can upload up to 10 images")
    private String[] images;

    @NotNull(message = "Amenities are required")
    @Size(max = 10, message = "You can upload up to 20 amenities")
    private String[] amenities;

    @NotNull(message = "Total rooms count is required")
    @Min(value = 1, message = "Total rooms count must be greater than 0")
    private Integer totalRoomsCount;

    @NotNull(message = "Capacity is required")
    @Min(value = 1, message = "Capacity must be greater than 0")
    private Integer capacity;
}
