package com.rohankumar.easylodge.dtos.user;

import com.rohankumar.easylodge.enums.gender.Gender;
import com.rohankumar.easylodge.enums.role.Role;
import lombok.*;
import java.util.Set;
import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserResponse {

    private UUID id;
    private String name;
    private String email;
    private Gender gender;
    private Set<Role> roles;
}
