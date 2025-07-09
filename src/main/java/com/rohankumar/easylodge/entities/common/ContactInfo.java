package com.rohankumar.easylodge.entities.common;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
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
