package com.rohankumar.easylodge.dtos.guest;

import com.rohankumar.easylodge.enums.gender.Gender;
import lombok.*;
import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GuestResponse {

    private UUID id;
    private String name;
    private Gender gender;
    private Integer age;
}
