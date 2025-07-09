package com.rohankumar.easylodge.entities.hotel;

import com.rohankumar.easylodge.entities.common.ContactInfo;
import com.rohankumar.easylodge.entities.common.DateAudit;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.UuidGenerator;
import java.util.UUID;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "hotels")
public class Hotel extends DateAudit {

    @Id
    @GeneratedValue
    @UuidGenerator
    private UUID id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private Boolean active;

    @Column(name = "images", columnDefinition = "TEXT[]")
    private String[] images;

    @Column(name = "amenities", columnDefinition = "TEXT[]")
    private String[] amenities;

    @Embedded
    private ContactInfo contactInfo;
}
