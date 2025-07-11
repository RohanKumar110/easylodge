package com.rohankumar.easylodge.repositories.hotel;

import com.rohankumar.easylodge.entities.hotel.Hotel;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface HotelRepository extends JpaRepository<Hotel, UUID> {

    @EntityGraph(attributePaths = "rooms")
    Optional<Hotel> findHotelWithRoomsById(UUID id);
}
