package com.rohankumar.easylodge.repositories.booking;

import com.rohankumar.easylodge.entities.booking.Booking;
import com.rohankumar.easylodge.entities.hotel.Hotel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.UUID;

@Repository
public interface BookingRepository extends JpaRepository<Booking, UUID> {

    Booking findBySessionId(String id);

    List<Booking> findByHotel(Hotel hotel);
}
