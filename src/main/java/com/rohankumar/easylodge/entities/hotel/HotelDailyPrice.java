package com.rohankumar.easylodge.entities.hotel;

import com.rohankumar.easylodge.entities.common.DateAudit;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.UuidGenerator;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "hotel_daily_price")
public class HotelDailyPrice extends DateAudit {

    @Id
    @GeneratedValue
    @UuidGenerator
    private UUID id;

    @ManyToOne(fetch =  FetchType.LAZY)
    @JoinColumn(name = "hotel_id", nullable = false)
    private Hotel hotel;

    @Column(nullable = false)
    private LocalDate date;

    // Lowest Room Price on a particular day
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal price;

    public HotelDailyPrice(Hotel hotel, LocalDate date) {

        this.hotel = hotel;
        this.date = date;
    }
}
