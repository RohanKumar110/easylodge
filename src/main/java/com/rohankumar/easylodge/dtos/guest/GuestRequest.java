package com.rohankumar.easylodge.dtos.guest;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.rohankumar.easylodge.enums.gender.Gender;
import jakarta.validation.constraints.*;
import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class GuestRequest {

    @NotBlank(message = "Name is required")
    @Size(min = 3, max = 50, message = "Name must be between 3 and 50 characters")
    @Pattern(regexp = "^[A-Za-z ]+$", message = "Name can only contain alphabets and spaces")
    private String name;

    @NotNull(message = "Date of Birth is required")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate dateOfBirth;

    @NotNull(message = "Gender is required")
    private Gender gender;
}
