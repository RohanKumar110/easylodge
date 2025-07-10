package com.rohankumar.easylodge.entities.hotel;

import com.rohankumar.easylodge.entities.common.ContactInfo;
import com.rohankumar.easylodge.entities.common.DateAudit;
import com.rohankumar.easylodge.entities.room.Room;
import com.rohankumar.easylodge.entities.user.User;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.UuidGenerator;

import java.util.List;
import java.util.UUID;

@Entity
@Getter
@Setter
@Builder
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

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User owner;

    @OneToMany(mappedBy = "hotel", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<Room> rooms;
}
