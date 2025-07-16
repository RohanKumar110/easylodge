package com.rohankumar.easylodge.dtos.authentication;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.rohankumar.easylodge.enums.role.Role;
import lombok.*;
import java.util.Set;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuthenticationResponse {

    private String accessToken;
    @JsonIgnore
    private String refreshToken;
    private Set<Role> roles;
}
