package com.rohankumar.easylodge.dtos.guest;

import com.rohankumar.easylodge.enums.gender.Gender;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class GuestRequest {

    private String name;
    private Gender gender;
    private Integer age;
}
