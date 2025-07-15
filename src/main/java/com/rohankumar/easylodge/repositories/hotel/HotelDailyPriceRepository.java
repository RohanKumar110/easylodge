package com.rohankumar.easylodge.repositories.hotel;

import com.rohankumar.easylodge.dtos.hotel.price.HotelPriceResponse;
import com.rohankumar.easylodge.entities.hotel.Hotel;
import com.rohankumar.easylodge.entities.hotel.HotelDailyPrice;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface HotelDailyPriceRepository extends JpaRepository<HotelDailyPrice, UUID> {

    Optional<HotelDailyPrice> findByHotelAndDate(Hotel hotel, LocalDate date);

    @Query("""
        SELECT new com.rohankumar.easylodge.dtos.hotel.price.HotelPriceResponse(hdp.hotel, AVG(hdp.price))
        FROM HotelDailyPrice hdp
        WHERE hdp.hotel.contactInfo.city = :city
          AND (hdp.date >= :startDate AND hdp.date < :endDate)
          AND hdp.hotel.active = TRUE
        GROUP BY hdp.hotel
        HAVING COUNT(hdp.date) = :requiredNights
    """)
    Page<HotelPriceResponse> findAvailableHotelsFromDailyPrice(
            String city,
            LocalDate startDate,
            LocalDate endDate,
            Long requiredNights,
            Pageable pageable
    );
}
