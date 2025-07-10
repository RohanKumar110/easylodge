package com.rohankumar.easylodge.entities.room;

import com.rohankumar.easylodge.entities.common.DateAudit;
import com.rohankumar.easylodge.entities.hotel.Hotel;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.UuidGenerator;
import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "rooms")
public class Room extends DateAudit {

    @Id
    @GeneratedValue
    @UuidGenerator
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "hotel_id", nullable = false)
    private Hotel hotel;

    @Column(nullable = false)
    private String type;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal basePrice;

    @Column(name = "images", columnDefinition = "TEXT[]")
    private String[] images;

    @Column(name = "amenities", columnDefinition = "TEXT[]")
    private String[] amenities;

    @Column(nullable = false)
    private Integer totalRoomsCount;

    @Column(nullable = false)
    private Integer capacity;
}
