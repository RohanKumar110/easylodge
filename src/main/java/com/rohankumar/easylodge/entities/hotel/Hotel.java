package com.rohankumar.easylodge.entities.hotel;

import com.rohankumar.easylodge.entities.common.ContactInfo;
import com.rohankumar.easylodge.entities.common.DateAudit;
import com.rohankumar.easylodge.entities.room.Room;
import com.rohankumar.easylodge.entities.user.User;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;
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
@SQLDelete(sql = "UPDATE hotels SET deleted = true WHERE id = ?")
@SQLRestriction("deleted = false")
public class Hotel extends DateAudit {

    @Id
    @GeneratedValue
    @UuidGenerator
    private UUID id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private Boolean active = Boolean.FALSE;

    @Column(name = "images", columnDefinition = "TEXT[]")
    private String[] images;

    @Column(name = "amenities", columnDefinition = "TEXT[]")
    private String[] amenities;

    @Embedded
    private ContactInfo contactInfo;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User owner;

    @OneToMany(mappedBy = "hotel", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<Room> rooms;

    @Column(nullable = false, columnDefinition = "BOOLEAN DEFAULT FALSE")
    private Boolean deleted = Boolean.FALSE;
}
