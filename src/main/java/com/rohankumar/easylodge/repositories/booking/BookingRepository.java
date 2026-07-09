package com.rohankumar.easylodge.repositories.booking;

import com.rohankumar.easylodge.dtos.hotel.report.HotelReportResponse;
import com.rohankumar.easylodge.entities.booking.Booking;
import com.rohankumar.easylodge.entities.hotel.Hotel;
import com.rohankumar.easylodge.entities.room.Room;
import com.rohankumar.easylodge.entities.user.User;
import com.rohankumar.easylodge.enums.booking.BookingStatus;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface BookingRepository extends JpaRepository<Booking, UUID> {

    Booking findBySessionId(String id);

    List<Booking> findByHotel(Hotel hotel);

    List<Booking> findByUser(User user);

    @Query("SELECT DISTINCT b FROM Booking b " +
            "JOIN FETCH b.room JOIN FETCH b.user JOIN FETCH b.hotel " +
            "LEFT JOIN FETCH b.guests " +
            "WHERE b.hotel.id = :hotelId")
    List<Booking> findByHotelIdWithDetails(UUID hotelId);

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

    @Query("""
        SELECT b
        FROM Booking b
        WHERE b.user = :user
          AND b.hotel = :hotel
          AND b.room = :room
          AND b.checkInDate = :checkInDate
          AND b.checkOutDate = :checkOutDate
          AND b.numberOfRooms = :numberOfRooms
          AND b.status = :status
    """)
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<Booking> findExistingReservedBooking(
            @Param("user") User user,
            @Param("hotel") Hotel hotel,
            @Param("room") Room room,
            @Param("checkInDate") LocalDate checkInDate,
            @Param("checkOutDate") LocalDate checkOutDate,
            @Param("numberOfRooms") Integer numberOfRooms,
            @Param("status") BookingStatus status
    );
}
