package com.rohankumar.easylodge.entities.common;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.*;

@Getter
@Setter
@Builder
@Embeddable
@NoArgsConstructor
@AllArgsConstructor
public class ContactInfo {

    private String email;

    @Column(nullable = false)
    private String phone;

    @Column(nullable = false)
    private String city;

    private String coordinates;

    @Column(nullable = false)
    private String address;
}
