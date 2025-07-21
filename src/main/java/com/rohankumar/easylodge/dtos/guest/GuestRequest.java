package com.rohankumar.easylodge.dtos.guest;

import com.rohankumar.easylodge.enums.gender.Gender;
import jakarta.validation.constraints.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class GuestRequest {

    @NotBlank(message = "Name is required")
    @Size(min = 3, max = 50, message = "Name must be between 3 and 50 characters")
    @Pattern(regexp = "^[A-Za-z ]+$", message = "Name can only contain alphabets and spaces")
    private String name;

    @NotNull(message = "Age is required")
    @Min(value = 1, message = "Age must be greater than zero")
    private Integer age;

    @NotNull(message = "Gender is required")
    private Gender gender;
}
