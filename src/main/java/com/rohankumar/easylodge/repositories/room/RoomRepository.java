package com.rohankumar.easylodge.repositories.room;

import com.rohankumar.easylodge.entities.room.Room;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.UUID;

@Repository
public interface RoomRepository extends JpaRepository<Room, UUID> {
}
