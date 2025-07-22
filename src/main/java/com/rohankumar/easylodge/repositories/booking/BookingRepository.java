package com.rohankumar.easylodge.repositories.booking;

import com.rohankumar.easylodge.dtos.hotel.report.HotelReportResponse;
import com.rohankumar.easylodge.entities.booking.Booking;
import com.rohankumar.easylodge.entities.hotel.Hotel;
import com.rohankumar.easylodge.enums.booking.BookingStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Repository
public interface BookingRepository extends JpaRepository<Booking, UUID> {

    Booking findBySessionId(String id);

    List<Booking> findByHotel(Hotel hotel);

    @Query("""
        SELECT new com.rohankumar.easylodge.dtos.hotel.report.HotelReportResponse(COUNT(b), SUM(b.amount), AVG(b.amount))
        FROM Booking b
        WHERE b.hotel = :hotel
            AND b.status = :status
            AND (b.createdAt >= :startDateTime AND b.createdAt <= :endDateTime)
    """)
    HotelReportResponse findHotelReportByStatusAndDateRange(
            Hotel hotel,
            BookingStatus status,
            LocalDateTime startDateTime,
            LocalDateTime endDateTime
    );
}
