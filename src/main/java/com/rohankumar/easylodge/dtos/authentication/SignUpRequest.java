package com.rohankumar.easylodge.dtos.authentication;

import com.rohankumar.easylodge.enums.gender.Gender;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SignUpRequest {

    @NotBlank(message = "Name is required")
    @Size(min = 3, max = 50, message = "Name must be between 3 and 50 characters")
    @Pattern(regexp = "^[A-Za-z ]+$", message = "Name can only contain alphabets and spaces")
    private String name;

    @Email(message = "Invalid Email")
    @NotBlank(message = "Email is required")
    private String email;

    @NotBlank(message = "Password is required")
    @Pattern(
            regexp = "^(?=.*\\d).{8,16}$",
            message = "Password must be 8-16 characters long and contain at least one number"
    )
    private String password;

    @NotNull(message = "Gender is required")
    private Gender gender;
}
