package com.rohankumar.easylodge.dtos.authentication;

import com.rohankumar.easylodge.enums.gender.Gender;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SignUpRequest {

    private String name;
    private String email;
    private String password;
    private Gender gender;
}
