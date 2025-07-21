package com.rohankumar.easylodge.dtos.hotel;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class HotelRequest {

    @NotBlank(message = "Name is required")
    @Size(min = 3, message = "Name must be at least 3 characters long")
    private String name;

    private Boolean active;

    @NotNull(message = "Images are required")
    @Size(max = 10, message = "You can upload up to 10 images")
    private String[] images;

    @NotNull(message = "Amenities are required")
    @Size(max = 10, message = "You can upload up to 20 amenities")
    private String[] amenities;

    @NotBlank(message = "Email is required")
    @Email(message = "Invalid Email")
    private String email;

    @NotBlank(message = "Phone number is required")
    @Pattern(
            regexp = "^\\d{3}-\\d{3}-\\d{4}$",
            message = "Phone number must be in the format 123-456-7890"
    )
    private String phone;

    @NotBlank(message = "City is required")
    @Pattern(
            regexp = "^[\\p{L}0-9]+(?:[\\s'\\-.][\\p{L}0-9]+)*$",
            message = "City name can only contain letters, numbers, spaces, hyphens, apostrophes, and periods"
    )
    private String city;

    @NotBlank(message = "Coordinates is required")
    @Pattern(
            regexp = "^\\s*([+-]?([1-8]?\\d(\\.\\d+)?|90(\\.0+)?))\\s*,\\s*([+-]?((1[0-7]\\d)|([1-9]?\\d))(\\.\\d+)?|180(\\.0+)?)\\s*$",
            message = "Coordinates must be in 'lat,long' format with valid latitude (-90 to 90) and longitude (-180 to 180)"
    )
    private String coordinates;

    @NotBlank(message = "Address is required")
    @Size(min = 5, max = 250, message = "Address must be between 5 and 250 characters")
    private String address;
}
